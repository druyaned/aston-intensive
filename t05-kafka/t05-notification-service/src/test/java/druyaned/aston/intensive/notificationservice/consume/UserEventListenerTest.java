package druyaned.aston.intensive.notificationservice.consume;

import druyaned.aston.intensive.notificationservice.NotificationServiceApp;
import druyaned.aston.intensive.notificationservice.message.MessageHandler;
import static druyaned.aston.intensive.userservice.notify.KafkaProducerConfig.USER_EVENTS_TOPIC;
import druyaned.aston.intensive.userservice.notify.UserEvent;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(classes = NotificationServiceApp.class)
@EmbeddedKafka(topics = USER_EVENTS_TOPIC, partitions = 1)
@TestPropertySource(properties = "kafka.brokers=${spring.embedded.kafka.brokers}")
public class UserEventListenerTest {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @MockitoBean
    private MessageHandler messageHandler;

    private Producer<String, UserEvent> producer;

    @BeforeEach
    public void setUpTestMethod(@Value("${kafka.brokers}") String kafkaBrokers) {

        Map<String, Object> producerConfigs = new HashMap<>(KafkaTestUtils
                .producerProps(embeddedKafkaBroker));

        producerConfigs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBrokers);
        producerConfigs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerConfigs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        producer = new KafkaProducer<>(producerConfigs);
    }

    @AfterEach
    public void cleanUpTestMethod() {
        if (producer != null) {
            producer.close();
        }
    }

    @Test
    public void sendCreateUserEventShouldBeHandledByListener() throws Exception {
        String emailKey = "email@example.com";
        UserEvent userEvent = new UserEvent(UserEvent.Type.CREATE, 1L);

        producer.send(new ProducerRecord<>(USER_EVENTS_TOPIC, emailKey, userEvent));
        producer.flush();

        verify(messageHandler, timeout(1000)).handle(eq(emailKey),
                eq("Hello! You account has been CREATED by ID=" + userEvent.id()));
    }

    @Test
    public void sendDeleteUserEventShouldBeHandledByListener() throws Exception {
        String emailKey = "email@example.com";
        UserEvent userEvent = new UserEvent(UserEvent.Type.DELETE, 2L);

        producer.send(new ProducerRecord<>(USER_EVENTS_TOPIC, emailKey, userEvent));
        producer.flush();

        verify(messageHandler, timeout(1000)).handle(eq(emailKey),
                eq("Hello! You account has been DELETED by ID=" + userEvent.id()));
    }
}
