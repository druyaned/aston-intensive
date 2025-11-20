package druyaned.aston.intensive.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.PropertySource;

/**
 * Task#05: "Add Swagger documentation and HATEOAS to the API".
 *
 * <p>
 * <b>Task requirements</b>:
 * <li>Document the existing API (from Task#04) using Swagger (Springdoc OpenAPI) so that you can
 * easily explore and test the API through the web interface</li>
 * <li>Add HATEOAS support so that the API provides links for navigating through resources</li>
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
