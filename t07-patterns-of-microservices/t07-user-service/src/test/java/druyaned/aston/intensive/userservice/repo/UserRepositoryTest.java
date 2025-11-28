package druyaned.aston.intensive.userservice.repo;

import druyaned.aston.intensive.userservice.model.UserEntity;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Integration tests of {@link UserRepository}.
 *
 * <p>
 * Step#15: activate test profile.
 *
 * @author druyaned
 */
@DataJpaTest(properties = {
    // DB connection
    "spring.datasource.driver-class-name=org.postgresql.Driver",
    "spring.datasource.driver-class-name=org.postgresql.Driver",
    // JPA and Hibernate config
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.defer-datasource-initialization=true",
    "spring.jpa.show-sql=true",
    "spring.sql.init.mode=always",
    "spring.sql.init.data-locations=classpath:/data-test.sql"
})
@Testcontainers
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> POSTGRES
            = new PostgreSQLContainer<>("postgres:18-alpine");

    @Autowired
    private UserRepository userRepo;

    @DynamicPropertySource
    public static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Test
    public void findAllUsersAndCheckTheSize() {
        Stream<UserEntity> userStream = StreamSupport.stream(userRepo.findAll().spliterator(),
                false);
        assertEquals(6L, userStream.count());
    }

    @Test
    public void checkForEmailExistence() {
        String existingEmail = "ira@eli.pali";
        assertTrue(userRepo.existsByEmail(existingEmail));
    }

    @Test
    public void checkForEmailNonExistence() {
        String nonExistingEmail = "not@existing.email";
        assertFalse(userRepo.existsByEmail(nonExistingEmail));
    }
}
