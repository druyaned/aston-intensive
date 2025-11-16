package druyaned.aston.intensive.userservice;

import druyaned.aston.intensive.userservice.console.AppConsole;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import static jakarta.persistence.Persistence.createEntityManagerFactory;
import static jakarta.validation.Validation.buildDefaultValidatorFactory;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

/**
 * Task#03: "Write unit-tests and integration tests for the user-service".
 *
 * <p>
 * <b>Task requirements</b>:
 * <ul>
 * <li>Use JUnit 5, Mockito and Testcontainers</li>
 * <li>To test the DAO layer, write integration tests using Testcontainers</li>
 * <li>To test the Service layer, write unit tests using Mockito</li>
 * <li>The tests should be isolated from each other</li>
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
public class App {

    private static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {
        try (
                ValidatorFactory vf = buildDefaultValidatorFactory();
                EntityManagerFactory emf = createEntityManagerFactory(
                        "UserService",
                        dbConnectionMap());
                EntityManager entityManager = emf.createEntityManager();) {

            Validator validator = vf.getValidator();
            AppConsole console = new AppConsole(System.out, System.err,
                    SCANNER, entityManager, validator);

            console.run();

        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }

    private static Map dbConnectionMap() throws IOException {
        Properties dbConnectionProperties = new Properties();
        dbConnectionProperties.load(App.class
                .getResourceAsStream("/db-connection.properties"));

        Map connectionMap = new LinkedHashMap();
        connectionMap.put("jakarta.persistence.jdbc.url",
                dbConnectionProperties.get("spring.datasource.url"));
        connectionMap.put("jakarta.persistence.jdbc.user",
                dbConnectionProperties.get("spring.datasource.username"));
        connectionMap.put("jakarta.persistence.jdbc.password",
                dbConnectionProperties.get("spring.datasource.password"));

        return connectionMap;
    }
}
