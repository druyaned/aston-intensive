package druyaned.aston.intensive.userservice.notify;

/**
 * The task requires to send some notification when a user is created or deleted; so this class
 * serves to encapsulate the operation and the user that the operation was performed on.
 *
 * <p>
 * In terms of Kafka this class can be named as event or record.
 */
public record UserEvent(Type type, Long id) {

    /**
     * Operation type: create or delete.
     */
    public static enum Type {CREATE, DELETE};
}
