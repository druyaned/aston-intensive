package druyaned.aston.intensive.userservice.notify;

import druyaned.aston.intensive.userevents.UserEvent;
import druyaned.aston.intensive.userservice.model.UserDto;
import druyaned.aston.intensive.userservice.serve.UserService;
import druyaned.aston.intensive.userservice.serve.UserService.Result;
import druyaned.aston.intensive.userservice.web.UserController;
import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Prep#02: Integration tests for {@link KafkaAspect} class. Spring Boot implicitly uses
 * {@code @EnableAspectJAutoProxy}. But some auto-configurations are useless, e.g.
 * {@code DataSourceAutoConfiguration}, so they are disabled in the properties file.
 *
 * @author druyaned
 */
@SpringBootTest
@TestPropertySource("classpath:/kafka-aspect-test.properties")
public class KafkaAspectTest {

    @MockitoBean
    private UserService userService;

    @Autowired
    private UserController userController;

    @MockitoBean
    private KafkaTemplate<String, UserEvent> kafkaTemplate;

    @Value("${topics.userEvents.name}")
    private String userEventsTopic;

    @Test
    public void aspectIsNotInvokedByFailingCreation() {
        UserDto userDto = makeUserDto();
        Result createdResult = Result.emailDuplication(userDto.getEmail());

        when(userService.create(eq(userDto))).thenReturn(createdResult);
        when(kafkaTemplate.send(anyString(), anyString(), any(UserEvent.class)))
                .thenReturn(any(CompletableFuture.class));

        userController.create(userDto);

        verify(userService).create(eq(userDto));
        verify(kafkaTemplate, never()).send(anyString(), anyString(), any(UserEvent.class));
    }

    @Test
    public void aspectIsInvokedByCreation() {
        UserDto userDto = makeUserDto();
        Result createdResult = Result.created(userDto);
        UserEvent event = UserEvent.creation(userDto.getId());

        when(userService.create(eq(userDto))).thenReturn(createdResult);
        when(kafkaTemplate.send(eq(userEventsTopic), eq(userDto.getEmail()), eq(event)))
                .thenReturn(any(CompletableFuture.class));

        userController.create(userDto);

        verify(userService).create(eq(userDto));
        verify(kafkaTemplate).send(eq(userEventsTopic), eq(userDto.getEmail()), eq(event));
    }

    @Test
    public void aspectIsNotInvokedByFailingDeletion() {
        UserDto userDto = makeUserDto();
        Result deleteResult = Result.notFound(userDto.getId());

        when(userService.delete(eq(userDto.getId()))).thenReturn(deleteResult);
        when(kafkaTemplate.send(anyString(), anyString(), any(UserEvent.class)))
                .thenReturn(any(CompletableFuture.class));

        userController.delete(userDto.getId());

        verify(userService).delete(eq(userDto.getId()));
        verify(kafkaTemplate, never()).send(anyString(), anyString(), any(UserEvent.class));
    }

    @Test
    public void aspectIsInvokedByDeletion() {
        UserDto userDto = makeUserDto();
        Result deleteResult = Result.deleted(userDto);
        UserEvent event = UserEvent.deletion(userDto.getId());

        when(userService.delete(eq(userDto.getId()))).thenReturn(deleteResult);
        when(kafkaTemplate.send(eq(userEventsTopic), eq(userDto.getEmail()), eq(event)))
                .thenReturn(any(CompletableFuture.class));

        userController.delete(userDto.getId());

        verify(userService).delete(eq(userDto.getId()));
        verify(kafkaTemplate).send(eq(userEventsTopic), eq(userDto.getEmail()), eq(event));
    }

    private UserDto makeUserDto() {
        UserDto user = new UserDto();

        user.setId(1L);
        user.setName("Max");
        user.setEmail("max@example.com");
        user.setCreatedAt(OffsetDateTime.parse("2025-10-26T03:30:14.172079+03"));

        return user;
    }
}
