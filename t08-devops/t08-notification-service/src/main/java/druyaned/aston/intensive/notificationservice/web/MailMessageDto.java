package druyaned.aston.intensive.notificationservice.web;

import java.util.Objects;

/**
 * Data Transfer Object for {@link SendMailController} that wraps email and message.
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

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.email);
        hash = 97 * hash + Objects.hashCode(this.message);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MailMessageDto other = (MailMessageDto) obj;
        if (!Objects.equals(this.email, other.email)) {
            return false;
        }
        return Objects.equals(this.message, other.message);
    }

    @Override
    public String toString() {
        return "MailMessageDto{" + "email=" + email + ", message=" + message + '}';
    }
}
