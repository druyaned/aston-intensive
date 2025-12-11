package druyaned.aston.intensive.notificationservice.message;

import druyaned.aston.intensive.notificationservice.consume.UserEventListener;

/**
 * Serves to handle messages.
 *
 * @author druyaned
 *
 * @see UserEventListener
 * @see MailMessageHandler
 */
public interface MessageHandler {

    /**
     * Handles the message by the email, exempli gratia sends the message to the email or prints
     * the message to a console.
     *
     * @param email recipient
     * @param message to be handled
     *
     * @throws Exception if something unexpected happens
     *
     * @see UserEventListener
     */
    void handle(String email, String message) throws Exception;
}
