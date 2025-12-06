package druyaned.aston.intensive.notificationservice.consume;

import druyaned.aston.intensive.notificationservice.message.MessageHandler;
import druyaned.aston.intensive.userevents.UserEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Provides Kafka listener for user events (created and deleted); creates a message and delegates to
 * {@link MessageHandler}.
 *
 * @author druyaned
 *
 * @see UserEvent
 */
@Component
public class UserEventListener {

    private final MessageHandler messageHandler;

    public UserEventListener(@Qualifier("mailMessageHandler") MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    @KafkaListener(topics = "${kafka.topics.user-events.name}")
    public void onUserEvent(ConsumerRecord<String, UserEvent> record) throws Exception {
        String email = record.key();
        UserEvent event = record.value();

        String message = "Hello, %s! You account is %s by ID=%d"
                .formatted(event.name(), event.type(), event.id());

        messageHandler.handle(email, message);
    }
}
