package druyaned.aston.intensive.notificationservice;

import druyaned.aston.intensive.userevents.UserEvent;
import java.io.PrintStream;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Application to handle {@link UserEvent user events} and to send mails.
 *
 * <p>
 * Prep#04: some changes and documentations are written for each class in each subpackage here.
 *
 * @author druyaned
 */
@SpringBootApplication
@EnableKafka
public class NotificationServiceApp {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApp.class, args);
    }

    @Bean
    public PrintStream printStream() {
        return System.out;
    }
}
