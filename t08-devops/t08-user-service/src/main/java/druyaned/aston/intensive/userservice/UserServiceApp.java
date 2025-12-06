package druyaned.aston.intensive.userservice;

import druyaned.aston.intensive.userservice.model.UserEntity;
import druyaned.aston.intensive.userservice.web.UserController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

/**
 * Application to work with {@link UserEntity users}; provides CRUD operations in the
 * {@link UserController}.
 *
 * @author druyaned
 */
@SpringBootApplication
@EntityScan("druyaned.aston.intensive.userservice.model")
public class UserServiceApp {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApp.class, args);
    }
}
