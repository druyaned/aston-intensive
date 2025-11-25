package druyaned.aston.intensive.userservice.notify;

import druyaned.aston.intensive.userevents.UserEvent;
import druyaned.aston.intensive.userservice.model.UserDto;
import druyaned.aston.intensive.userservice.serve.UserService.Result;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Prep#01: Aspect encapsulates pointcuts (controller's create and delete methods) and advices (to
 * send corresponding events by Kafka).
 *
 * @author druyaned
 */
@Aspect
@Component
public class KafkaAspect {

    private final KafkaTemplate<String, UserEvent> kafkaTemplate;
    private final String userEventsTopic;

    public KafkaAspect(KafkaTemplate<String, UserEvent> kafkaTemplate,
            @Value("${topics.userEvents.name}") String userEventsTopic) {

        this.kafkaTemplate = kafkaTemplate;
        this.userEventsTopic = userEventsTopic;
    }

    @Around("execution(* *..UserController.create(*))")
    public Object sendCreateEvent(ProceedingJoinPoint joinPoint) throws Throwable {
        Object value = joinPoint.proceed();

        if (value instanceof ResponseEntity responseEntity
                && responseEntity.getStatusCode().equals(HttpStatus.CREATED)
                && responseEntity.getBody() instanceof Result createResult) {

            UserDto user = createResult.content();

            kafkaTemplate.send(userEventsTopic, user.getEmail(), UserEvent.creation(user.getId()));
        }

        return value;
    }

    @Around("execution(* *..UserController.delete(*))")
    public Object sendDeleteEvent(ProceedingJoinPoint joinPoint) throws Throwable {
        Object value = joinPoint.proceed();

        if (value instanceof ResponseEntity responseEntity
                && responseEntity.getStatusCode().equals(HttpStatus.OK)
                && responseEntity.getBody() instanceof Result deleteResult) {

            UserDto user = deleteResult.content();

            kafkaTemplate.send(userEventsTopic, user.getEmail(), UserEvent.deletion(user.getId()));
        }

        return value;
    }
}
