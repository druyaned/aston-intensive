package druyaned.aston.intensive.notificationservice;

import druyaned.aston.intensive.userevents.UserEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Application to handle {@link UserEvent user events} and to send mails.
 *
 * @author druyaned
 */
@SpringBootApplication
@EnableKafka
public class NotificationServiceApp {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApp.class, args);
    }
}
