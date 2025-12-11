package druyaned.aston.intensive.notificationservice.message;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Prep#03: {@link MessageHandler} of {@link Session}.
 *
 * @author druyaned
 */
@Component
public class MailMessageHandler implements MessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(MailMessageHandler.class);

    private final MailCredentials credentials;
    private final Session session;

    public MailMessageHandler(SmtpProperties smtp, MailCredentials credentials) throws IOException {
        this.credentials = credentials;

        Properties props = new Properties();
        props.put("mail.smtp.host", smtp.host());
        props.put("mail.smtp.port", smtp.port());
        props.put("mail.smtp.auth", smtp.auth());
        props.put("mail.smtp.starttls.enable", smtp.starttlsEnable());
        props.put("mail.smtp.ssl.enable", smtp.sslEnable());

        session = Session.getInstance(props);
    }

    /**
     * Sends the message to the email using {@link Session}.
     *
     * @param email recipient
     * @param message to be sent
     *
     * @throws AddressException if the parse failed
     * @throws MessagingException for other sending failures
     */
    @Override
    public void handle(String email, String message) throws AddressException, MessagingException {
        InternetAddress senderAddress = new InternetAddress(credentials.addr());
        InternetAddress recipientAddress = new InternetAddress(email);

        Message messageObj = new MimeMessage(session);
        messageObj.setFrom(senderAddress);

        messageObj.setRecipient(Message.RecipientType.TO, recipientAddress);

        messageObj.setSubject("Notification Service sends a message");
        messageObj.setText(message);

        Transport.send(messageObj, credentials.addr(), credentials.pass());

        logger.info("The message '" + message + "' was sent to '" + email + "'");
    }
}
