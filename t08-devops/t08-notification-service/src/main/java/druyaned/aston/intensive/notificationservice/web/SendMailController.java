package druyaned.aston.intensive.notificationservice.web;

import druyaned.aston.intensive.notificationservice.message.MailMessageHandler;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * API to send mails (task requirement) using {@link MailMessageHandler}.
 *
 * @author druyaned
 */
@RestController
@RequestMapping("/notify")
public class SendMailController {

    private final MailMessageHandler mailMessageHandler;

    public SendMailController(MailMessageHandler mailMessageHandler) {
        this.mailMessageHandler = mailMessageHandler;
    }

    @PostMapping
    public ResponseEntity<String> sendMail(@RequestBody MailMessageDto mailMessageDto) {
        String email = mailMessageDto.getEmail();
        String message = mailMessageDto.getMessage();

        try {
            mailMessageHandler.handle(email, message);

            return ResponseEntity.ok("Email is sent to " + email);

        } catch (MessagingException exc) {

            return ResponseEntity.internalServerError()
                    .body("Failed to send email: " + exc.getMessage());
        }
    }
}
