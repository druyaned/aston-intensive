package druyaned.aston.intensive.notificationservice.message;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Prep#03: configuration to work with {@code jakarta.mail}.
 *
 * @author druyaned
 *
 * @see MailMessageHandler
 * @see SmtpProperties
 * @see MailCredentials
 */
@Configuration
@PropertySource("classpath:/mail-connection.properties")
@EnableConfigurationProperties({SmtpProperties.class, MailCredentials.class})
public class MailConfig {}
