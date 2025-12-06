package druyaned.aston.intensive.notificationservice.message;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Prep#03: provides "mail.smtp" properties of the mail sender.
 *
 * @see MailConfig
 */
@ConfigurationProperties(prefix = "mail.smtp")
public record SmtpProperties(
        String host,
        int port,
        boolean auth,
        boolean starttlsEnable,
        boolean sslEnable) {}
