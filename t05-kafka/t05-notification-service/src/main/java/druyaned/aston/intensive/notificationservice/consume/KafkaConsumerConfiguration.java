package druyaned.aston.intensive.notificationservice.consume;

import static druyaned.aston.intensive.userservice.notify.KafkaProducerConfig.PARTITIONS_COUNT;
import druyaned.aston.intensive.userservice.notify.UserEvent;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
public class KafkaConsumerConfiguration {

    public static final String GROUP_ID = "notification-service";

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, UserEvent>>
            kafkaListenerContainerFactory(ConsumerFactory<String, UserEvent> consumerFactory) {

        ConcurrentKafkaListenerContainerFactory factory
                = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        // Next statement creates several parallel messageListenerContainers.
        // These consumers will then share the partitions of the subscribed topics
        factory.setConcurrency(PARTITIONS_COUNT);
        factory.getContainerProperties().setPollTimeout(3000);

        return factory;
    }

    @Bean
    public ConsumerFactory<String, UserEvent> consumerFactory(
            @Value("${kafka.brokers}") String kafkaBrokers,
            @Value("${auto.offset.reset}") String autoOffsetReset,
            @Value("${trusted.packages}") String trustedPackages) {

        Map<String, Object> configProps = new HashMap<>();

        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBrokers);
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, String.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, trustedPackages);
        configProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, UserEvent.class);

        return new DefaultKafkaConsumerFactory<>(configProps, new StringDeserializer(),
                new JsonDeserializer<>(UserEvent.class));
    }
}
