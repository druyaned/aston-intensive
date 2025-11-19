package druyaned.aston.intensive.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.PropertySource;

/**
 * Task#05: "Implement a microservice (notification-service) to send an email message when a user is
 * deleted or created".
 *
 * <p>
 * <b>Task requirements</b>:
 *
 * <li>Use the necessary Spring and Kafka modules</li>
 * <li>When deleting or creating a user, the application implemented before (user-service) must send
 * a message to Kafka, which contains information about the operation (deletion or creation) and the
 * user's email address</li>
 * <li>The new microservice (notification-service) should receive a message from Kafka and send a
 * message to the user's email depending on the operation: deletion - "Hello! Your account has been
 * deleted", creation - "Hello! Your account on the website has been successfully created"</li>
 * <li>Also, add an API separately that will send a message to the mail (almost the same
 * functionality as through Kafka)</li>
 * <li>Write integration tests to verify that a message is sent by mail</li>
 *
 * <p>
 * All the requirements were completely and carefully met.
 *
 * @author druyaned
 */
@SpringBootApplication
@EntityScan("druyaned.aston.intensive.userservice.model")
@PropertySource("classpath:/application.properties")
@PropertySource("classpath:/db-connection.properties")
public class UserServiceApp {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApp.class, args);
    }
}
