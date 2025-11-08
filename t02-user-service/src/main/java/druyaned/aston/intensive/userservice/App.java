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
 * Task#02: "Develop a Java console application (user-service) using Hibernate
 * to interact with PostgreSQL, without using Spring. The application must
 * support basic CRUD operations (Create, Read, Update, Delete) on the User
 * entity."
 *
 * <p>
 * <b>Task requirements</b>:
 * <ul>
 *   <li>Use Hibernate as an ORM</li>
 *   <li>The database is PostgreSQL</li>
 *   <li>Configure Hibernate without Spring using hibernate.cfg.xml or a
 *     properties file</li>
 *   <li>Implement CRUD operations for the User entity (create, read, update,
 *     delete), which consists of fields: id, name, email, age, created_at</li>
 *   <li>Use the console interface to interact with the user</li>
 *   <li>Use Maven for dependency management</li>
 *   <li>Set up logging</li>
 *   <li>Configure transactionality for database operations</li>
 *   <li>Use the DAO pattern to separate the logic of working with the
 *     database</li>
 *   <li>Handle possible exceptions related to Hibernate and PostgreSQL</li>
 * </ul>
 *
 * <p>
 * All tasks are carefully solved. There are some comments across the project
 * which can be looked up by "{@code task requirement}" (ignore case).
 *
 * <p>
 * There were alternatives: Hibernate API or JPA API. The choice fell on
 * JPA, because it's more relevant for these topic and task.
 *
 * <p>
 * The main method creates a {@link Validator} (there was no way not to use
 * it here), {@link EntityManager}, and runs the {@link AppConsole app}.
 *
 * <p>
 * <b>How to run</b>:
 * <ol>
 *   <li>PostgreSQL database must be installed</li>
 *   <li>Execute the script "db-create.sql" in the resources directory</li>
 *   <li>Run the app using command line tool</li>
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
