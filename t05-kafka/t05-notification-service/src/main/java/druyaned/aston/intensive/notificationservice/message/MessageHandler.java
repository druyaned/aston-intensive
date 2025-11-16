package druyaned.aston.intensive.notificationservice.message;

public interface MessageHandler {

    void handle(String email, String message) throws Exception;
}
