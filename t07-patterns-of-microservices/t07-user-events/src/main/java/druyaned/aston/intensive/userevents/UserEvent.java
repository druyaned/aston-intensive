package druyaned.aston.intensive.userevents;

/**
 * The task requires to send some notification when a user is created or deleted; so this class
 * serves to encapsulate the operation and the user that the operation was performed on.
 *
 * <p>
 * In terms of Kafka this class can be named as event or record.
 */
public record UserEvent(Type type, Long id) {

    /**
     * Operation type: creation or deletion.
     */
    public static enum Type {CREATE, DELETE};

    /**
     * Makes a new user event of user creation.
     *
     * @param id user id
     * @return new user event of user creation
     */
    public static UserEvent creation(Long id) {
        return new UserEvent(Type.CREATE, id);
    }

    /**
     * Makes a new user event of user deletion.
     *
     * @param id user id
     * @return new user event of user deletion
     */
    public static UserEvent deletion(Long id) {
        return new UserEvent(Type.DELETE, id);
    }
}
