package druyaned.aston.intensive.notificationservice.consume;

import druyaned.aston.intensive.notificationservice.message.MessageHandler;
import druyaned.aston.intensive.notificationservice.web.MailMessageDto;
import druyaned.aston.intensive.notificationservice.web.SendMailController;
import druyaned.aston.intensive.userevents.UserEvent;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Integration tests of {@link UserEventListener}.
 *
 * @author druyaned
 */
@SpringBootTest(properties = {
    "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer",
    "spring.kafka.producer.value-serializer="
            + "org.springframework.kafka.support.serializer.JsonSerializer"
})
@EmbeddedKafka(topics = "${kafka.topics.user-events.name}", partitions = 1)
@ActiveProfiles("test")
public class UserEventListenerTest {

    @Value("${kafka.topics.user-events.name}")
    private String userEventsTopic;

    @Autowired
    private KafkaTemplate<String, UserEvent> kafkaTemplate;

    @MockitoBean
    private MessageHandler messageHandler;

    // This bean is here to aviod an exception that is thrown by @SpringBootTest which tries to
    // create the controller that depends on mocked bean messageHandler
    @MockitoBean
    private SendMailController controller;

    @Test
    public void sendCreateUserEventShouldBeHandledByListener() throws Exception {
        String emailKey = "max@verstapen.or.not.com";
        UserEvent event = UserEvent.created("Max", 1L);
        String expectedMessage = "Hello, %s! You account is %s by ID=%d"
                .formatted(event.name(), event.type(), event.id());

        doNothing().when(messageHandler).handle(anyString(), anyString());

        kafkaTemplate.send(userEventsTopic, emailKey, event);
        kafkaTemplate.flush();

        verify(messageHandler, timeout(1000)).handle(eq(emailKey), eq(expectedMessage));
        verify(controller, never()).sendMail(any(MailMessageDto.class));
    }

    @Test
    public void sendDeleteUserEventShouldBeHandledByListener() throws Exception {
        String emailKey = "max@verstapen.or.not.com";
        UserEvent event = UserEvent.deleted("Max", 2L);
        String expectedMessage = "Hello, %s! You account is %s by ID=%d"
                .formatted(event.name(), event.type(), event.id());

        doNothing().when(messageHandler).handle(anyString(), anyString());

        kafkaTemplate.send(userEventsTopic, emailKey, event);
        kafkaTemplate.flush();

        verify(messageHandler, timeout(1000)).handle(eq(emailKey), eq(expectedMessage));
    }
}
