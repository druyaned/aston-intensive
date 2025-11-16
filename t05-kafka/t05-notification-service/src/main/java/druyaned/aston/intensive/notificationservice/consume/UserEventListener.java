 package druyaned.aston.intensive.notificationservice.consume;

import druyaned.aston.intensive.notificationservice.message.MessageHandler;
import static druyaned.aston.intensive.notificationservice.consume.KafkaConsumerConfiguration.GROUP_ID;
import static druyaned.aston.intensive.userservice.notify.KafkaProducerConfig.USER_EVENTS_TOPIC;
import druyaned.aston.intensive.userservice.notify.UserEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserEventListener {

    private final MessageHandler messageHandler;

    public UserEventListener(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    @KafkaListener(topics = USER_EVENTS_TOPIC, groupId = GROUP_ID)
    public void onUserEvent(ConsumerRecord<String, UserEvent> record) throws Exception {

        String email = record.key();
        UserEvent event = record.value();

        String message = switch (event.type()) {
            case CREATE -> "Hello! You account has been CREATED by ID=" + event.id();
            case DELETE -> "Hello! You account has been DELETED by ID=" + event.id();
            default -> "[UNKNOWN_EVENT_TYPE]";
        };

        messageHandler.handle(email, message);
    }
}
