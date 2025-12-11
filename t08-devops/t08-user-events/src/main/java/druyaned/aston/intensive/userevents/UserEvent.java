package druyaned.aston.intensive.userevents;

/**
 * The task requires to send some notification when a user is created or deleted; so this class
 * serves to encapsulate the operation and some user info that the operation was performed on.
 *
 * <p>
 * In terms of Kafka this class can be named as event or record.
 *
 * @see Type
 */
public record UserEvent(Type type, String name, Long id) {

    /**
     * Operation type: created or deleted.
     */
    public static enum Type {CREATED, DELETED};

    /**
     * Makes a new user event of user created.
     *
     * @param id user id
     * @return new user event of user created
     */
    public static UserEvent created(String name, Long id) {
        return new UserEvent(Type.CREATED, name, id);
    }

    /**
     * Makes a new user event of user deleted.
     *
     * @param id user id
     * @return new user event of user deleted
     */
    public static UserEvent deleted(String name, Long id) {
        return new UserEvent(Type.DELETED, name, id);
    }
}
