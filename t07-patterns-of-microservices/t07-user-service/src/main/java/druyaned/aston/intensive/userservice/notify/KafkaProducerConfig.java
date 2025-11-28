package druyaned.aston.intensive.userservice.notify;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Provides {@code user-events} topic with its configuration. All other factory and configuration
 * beans (exempli gratia {@code ProducerFactory}, {@code ConcurrentKafkaListenerContainerFactory}
 * and so on) are provided by Spring Boot.
 *
 * @author druyaned
 */
@Configuration
public class KafkaProducerConfig {

    @Bean
    public NewTopic userEventsTopic(@Value("${topics.userEvents.name}") String userEventsTopic,
            @Value("${topics.userEvents.partitionsCount}") int partitionsCount,
            @Value("${topics.userEvents.replicasCount}") int replicasCount) {

        return TopicBuilder
                .name(userEventsTopic)
                .partitions(partitionsCount)
                .replicas(replicasCount)
                .build();
    }
}
