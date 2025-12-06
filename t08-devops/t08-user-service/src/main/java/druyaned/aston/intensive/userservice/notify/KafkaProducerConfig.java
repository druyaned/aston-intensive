package druyaned.aston.intensive.userservice.notify;

import druyaned.aston.intensive.userevents.UserEventsProperties;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Prep#03: provides {@code user-events} topic with its configuration.
 *
 * <p>
 * Prep#04: The problem of the lack of a topic at the time of launch of the service, some kind of
 * Kafka client, has been resolved.
 *
 * @author druyaned
 */
@Configuration
public class KafkaProducerConfig {

    @Bean
    @ConfigurationProperties(prefix = "kafka.topics.user-events")
    public UserEventsProperties userEventsProperties() {
        return new UserEventsProperties();
    }

    @Bean
    public NewTopic userEventsTopic(UserEventsProperties userEventsProperties) {
        return TopicBuilder
                .name(userEventsProperties.getName())
                .partitions(userEventsProperties.getPartitionsCount())
                .replicas(userEventsProperties.getReplicasCount())
                .build();
    }
}
