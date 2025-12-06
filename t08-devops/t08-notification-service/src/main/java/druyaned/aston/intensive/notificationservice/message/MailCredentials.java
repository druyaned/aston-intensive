package druyaned.aston.intensive.notificationservice.message;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Prep#03: provides credentials of the mail sender.
 *
 * @see MailConfig
 */
@ConfigurationProperties(prefix = "send.mail")
public record MailCredentials(String addr, String pass) {}
