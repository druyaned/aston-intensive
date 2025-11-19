package druyaned.aston.intensive.notificationservice.consume;

import druyaned.aston.intensive.notificationservice.message.MessageHandler;
import druyaned.aston.intensive.userservice.notify.UserEvent;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer",
    "spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer"
})
@EmbeddedKafka(topics = "${topics.userEvents.name}", partitions = 1)
public class UserEventListenerTest {

    @Value("${topics.userEvents.name}")
    private String userEventsTopic;

    @Autowired
    private KafkaTemplate<String, UserEvent> kafkaTemplate;

    @MockitoBean
    private MessageHandler messageHandler;

    @Test
    public void sendCreateUserEventShouldBeHandledByListener() throws Exception {
        String emailKey = "email@example.com";
        UserEvent userEvent = new UserEvent(UserEvent.Type.CREATE, 1L);

        doNothing().when(messageHandler).handle(anyString(), anyString());

        kafkaTemplate.send(userEventsTopic, emailKey, userEvent);
        kafkaTemplate.flush();

        verify(messageHandler, timeout(1000)).handle(eq(emailKey),
                eq("Hello! You account has been CREATED by ID=" + userEvent.id()));
    }

    @Test
    public void sendDeleteUserEventShouldBeHandledByListener() throws Exception {
        String emailKey = "email@example.com";
        UserEvent userEvent = new UserEvent(UserEvent.Type.DELETE, 2L);

        doNothing().when(messageHandler).handle(anyString(), anyString());

        kafkaTemplate.send(userEventsTopic, emailKey, userEvent);
        kafkaTemplate.flush();

        verify(messageHandler, timeout(1000)).handle(eq(emailKey),
                eq("Hello! You account has been DELETED by ID=" + userEvent.id()));
    }
}
