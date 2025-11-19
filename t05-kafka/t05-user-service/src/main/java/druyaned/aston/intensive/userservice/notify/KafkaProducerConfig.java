package druyaned.aston.intensive.userservice.notify;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaProducerConfig {

    /*
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
    */

    @Bean
    public NewTopic userEventsTopic(@Value("${topics.userEvents.name}") String userEventsTopic,
            @Value("${topics.userEvents.partitionsCount}") int partitionsCount,
            @Value("${topics.userEvents.replicasCount}") int replicasCount) {

        System.out.println("userEventsTopic=" + userEventsTopic);// TODO: debug

        return TopicBuilder
                .name(userEventsTopic)
                .partitions(partitionsCount)
                .replicas(replicasCount)
                .build();
    }
}
