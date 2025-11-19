package druyaned.aston.intensive.notificationservice.web;

/**
 * Data Transfer Object for the {@link SendMailController} that wraps email and message.
 *
 * @author druyaned
 */
public class MailMessageDto {

    private String email;
    private String message;

    public MailMessageDto() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
