package druyaned.aston.intensive.notificationservice.message;

/**
 * Serves to handle messages that are gotten from the producer which is user-service.
 *
 * @author druyaned
 */
public interface MessageHandler {

    /**
     * Handles the message by the email that are gotten from the producer that is user-service.
     *
     * @param email key of the sending
     * @param message to be handled
     *
     * @throws Exception if something unexpected happens
     */
    void handle(String email, String message) throws Exception;
}
