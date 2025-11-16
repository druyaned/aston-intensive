package druyaned.aston.intensive.userservice.notify;

public record UserEvent(Type type, Long id) {

    public static enum Type {CREATE, DELETE};
}
