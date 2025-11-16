package druyaned.aston.intensive.notificationservice.web;

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
