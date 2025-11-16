package druyaned.aston.intensive.userservice.notify;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
public class KafkaProducerConfig {

    public static final String USER_EVENTS_TOPIC = "user.events";
    public static final int PARTITIONS_COUNT = 12;
    public static final int REPLICAS_COUNT = 2;

    @Bean
    public KafkaAdmin kafkaAdmin(@Value("${kafka.brokers}") String kafkaBrokers) {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBrokers);

        return new KafkaAdmin(configProps);
    }

    @Bean
    public ProducerFactory<String, UserEvent> kafkaProducerFactory(
            @Value("${kafka.brokers}") String kafkaBrokers) {

        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBrokers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, UserEvent> kafkaTemplate(
            ProducerFactory<String, UserEvent> producerFactory) {

        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public NewTopic userEventsTopic() {
        return TopicBuilder
                .name(USER_EVENTS_TOPIC)
                .partitions(PARTITIONS_COUNT)
                .replicas(REPLICAS_COUNT)
                .build();
    }
}
