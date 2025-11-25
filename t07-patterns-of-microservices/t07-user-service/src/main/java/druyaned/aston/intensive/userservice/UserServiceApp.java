package druyaned.aston.intensive.userservice;

import druyaned.aston.intensive.userservice.model.UserEntity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.PropertySource;

/**
 * Application to work with {@link UserEntity users}.
 *
 * <p>
 * Prep#04: some changes and documentations are written for each class in each subpackage here.
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
