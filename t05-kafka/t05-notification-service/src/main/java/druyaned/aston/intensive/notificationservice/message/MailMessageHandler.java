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
import org.springframework.stereotype.Component;

@Component
public class MailMessageHandler implements MessageHandler {

    private final String senderAddressStr;
    private final String applicationPassword;
    private final Session session;

    public MailMessageHandler() throws IOException {

        Properties connectionProps = new Properties();
        connectionProps.load(MailMessageHandler.class
                .getResourceAsStream("/mail-connection.properties"));

        senderAddressStr = connectionProps.getProperty("addr");
        applicationPassword = connectionProps.getProperty("pass");

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.yandex.ru");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.enable", "true");

        session = Session.getInstance(props);
    }

    @Override
    public void handle(String recipientEmail, String message) throws AddressException,
            MessagingException {

        InternetAddress senderAddress = new InternetAddress(senderAddressStr);
        InternetAddress recipientAddress = new InternetAddress(recipientEmail);

        Message messageObj = new MimeMessage(session);
        messageObj.setFrom(senderAddress);

        messageObj.setRecipient(Message.RecipientType.TO, recipientAddress);

        messageObj.setSubject("Notification Service sends a message");
        messageObj.setText(message);

        Transport.send(messageObj, senderAddressStr, applicationPassword);
    }
}
