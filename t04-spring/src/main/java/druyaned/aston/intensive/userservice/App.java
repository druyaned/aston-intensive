package druyaned.aston.intensive.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.PropertySource;

/**
 * Task#04: "Add Spring Framework to the user-service and develop API that will
 * allow data management".
 *
 * <p>
 * <b>Task requirements</b>:
 * <ul>
 * <li>Use necessary Spring modules (boot, web, data etc.)</li>
 * <li>Implement API to get, create, update and delete a user. It's important
 * that entity shouldn't be returned from the controller, it's necessary to use
 * DTO</li>
 * <li>Replace Hibernate with Spring Data JPA</li>
 * <li>Write tests for the API (you can do this using MockMvc or other
 * tools)</li>
 * </ul>
 *
 * <p>
 * All the requirements were completely and carefully met.
 *
 * <p>
 * <b>How to run</b>:
 * <ol>
 * <li>PostgreSQL database must be installed</li>
 * <li>Follow instructions in the "src/main/resources/db-create.sql"</li>
 * <li>Run the app using command line tool</li>
 * </ol>
 *
 * @author druyaned
 */
@SpringBootApplication
@EntityScan("druyaned.aston.intensive.userservice.model")
@PropertySource("classpath:/application.properties")
@PropertySource("classpath:/db-connection.properties")
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
