package druyaned.aston.intensive.userservice.dao;

import druyaned.aston.intensive.userservice.model.UserEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import static org.hibernate.cfg.CacheSettings.USE_QUERY_CACHE;
import static org.hibernate.cfg.CacheSettings.USE_SECOND_LEVEL_CACHE;
import org.hibernate.cfg.Configuration;
import static org.hibernate.cfg.JdbcSettings.FORMAT_SQL;
import static org.hibernate.cfg.JdbcSettings.HIGHLIGHT_SQL;
import static org.hibernate.cfg.JdbcSettings.JAKARTA_JDBC_PASSWORD;
import static org.hibernate.cfg.JdbcSettings.JAKARTA_JDBC_URL;
import static org.hibernate.cfg.JdbcSettings.JAKARTA_JDBC_USER;
import static org.hibernate.cfg.SchemaToolingSettings.JAKARTA_HBM2DDL_DATABASE_ACTION;
import static org.hibernate.tool.schema.Action.CREATE_DROP;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Test class of the DAO layer.
 *
 * <p>
 * <b>Task#03 requirement that are met here</b>:
 * <ol>
 * <li>To test the DAO layer, write integration tests using Testcontainers</li>
 * <li>The tests should be isolated from each other</li>
 * </ol>
 *
 * <p>
 * I could use {@code @Testcontainers} for the class and {@code @Container} for
 * the PostgreSQLContainer static instance. But I've made a decision to apply
 * programmatic approach here, cause it's more natural here and also helps to
 * figure out the execution better.
 *
 * @author druyaned
 */
public class UserDaoImplTest {

    private static PostgreSQLContainer<?> postgres;
    private static EntityManagerFactory emf;

    private EntityManager entityManager;
    private UserDaoImpl userDaoImpl;

    @BeforeAll
    public static void setUpTestClass() {
        postgres = new PostgreSQLContainer<>("postgres:18-alpine");
        postgres.start();

        emf = new Configuration()
                .addAnnotatedClass(UserEntity.class)
                // Database connection
                .setProperty(JAKARTA_JDBC_URL, postgres.getJdbcUrl())
                .setProperty(JAKARTA_JDBC_USER, postgres.getUsername())
                .setProperty(JAKARTA_JDBC_PASSWORD, postgres.getPassword())
                // System.out logging
                .setProperty(FORMAT_SQL, true)
                .setProperty(HIGHLIGHT_SQL, true)
                // Explicitly signals: there is no second-level and query cache
                .setProperty(USE_SECOND_LEVEL_CACHE, false)
                .setProperty(USE_QUERY_CACHE, false)
                // Schema generation strategy
                .setProperty(JAKARTA_HBM2DDL_DATABASE_ACTION, CREATE_DROP)
                .buildSessionFactory();
    }

    @AfterAll
    public static void cleanUpTestClass() {
        emf.close();
        postgres.stop();
    }

    @BeforeEach
    public void setUpTestMethod() {
        entityManager = emf.createEntityManager();
        userDaoImpl = new UserDaoImpl(entityManager);
    }

    @AfterEach
    public void cleanUpTestMethod() {
        entityManager.close();
    }

    @Test
    public void saveUpdateAndDeleteCorrectUsers() {
        UserEntity joe = makeJoe();
        UserEntity ira = makeIra();

        userDaoImpl.save(joe);
        userDaoImpl.save(ira);

        List<UserEntity> users = userDaoImpl.findAll();
        assertEquals(2, users.size());
        assertTrue(users.contains(joe));
        assertTrue(users.contains(ira));
        assertNotNull(users.get(0).getId());
        assertNotNull(users.get(1).getId());

        assertNotEquals("joe@alo.priem", joe.getEmail());
        joe.setEmail("joe@alo.priem");
        userDaoImpl.update(joe);

        UserEntity foundJoe = userDaoImpl.find(joe.getId());
        assertEquals("joe@alo.priem", foundJoe.getEmail());

        assertNull(ira.getBirthdate());
        LocalDate newBirthdate = LocalDate.of(1993, 5, 20);
        ira.setBirthdate(newBirthdate);
        userDaoImpl.update(ira);

        UserEntity foundIra = userDaoImpl.find(ira.getId());
        assertEquals(newBirthdate, foundIra.getBirthdate());

        assertEquals(2, userDaoImpl.findAll().size());
        userDaoImpl.delete(joe);
        userDaoImpl.delete(ira);

        assertEquals(0, userDaoImpl.findAll().size());
    }

    @Test
    public void saveUserWithNullEmailShouldThrowException() {
        UserEntity joe = makeJoe();
        joe.setEmail(null);
        assertThrows(ConstraintViolationException.class,
                () -> userDaoImpl.save(joe));
    }

    @Test
    public void saveUserWithExistingEmailShouldThrowException() {
        UserEntity joe = makeJoe();
        userDaoImpl.save(joe);

        String existingEmail = joe.getEmail();
        UserEntity ira = makeIra();
        ira.setEmail(existingEmail);

        assertThrows(org.hibernate.exception.ConstraintViolationException.class,
                () -> userDaoImpl.save(ira));

        userDaoImpl.delete(joe);
    }

    @Test
    public void saveNullShouldThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> userDaoImpl.save(null));
    }

    @Test
    public void updateInvalidUser() {
        UserEntity joe = makeJoe();
        userDaoImpl.save(joe);

        joe.setEmail(null);
        assertThrows(ConstraintViolationException.class,
                () -> userDaoImpl.update(joe));

        userDaoImpl.delete(joe);
        assertEquals(0, userDaoImpl.findAll().size());
    }

    @Test
    public void findByMissingId() {
        assertNull(userDaoImpl.find(1L));
    }

    @Test
    public void deleteMissingId() {
        userDaoImpl.delete(makeJoe());
        assertEquals(0, userDaoImpl.findAll().size());
    }

    @Test
    public void checkExistingEmail() {
        UserEntity joe = makeJoe();
        userDaoImpl.save(joe);
        assertEquals(1, userDaoImpl.findAll().size());

        assertTrue(userDaoImpl.emailExists(joe.getEmail()));

        userDaoImpl.delete(joe);
        assertEquals(0, userDaoImpl.findAll().size());
    }

    @Test
    public void checkNonExistingEmail() {
        assertFalse(userDaoImpl.emailExists("some email"));
    }

    private UserEntity makeJoe() {
        UserEntity joe = new UserEntity();
        joe.setName("Joe");
        joe.setEmail("joe@gobanana.org");
        joe.setBirthdate(LocalDate.of(1992, 4, 19));
        joe.setCreatedAt(OffsetDateTime.now());

        return joe;
    }

    private UserEntity makeIra() {
        UserEntity ira = new UserEntity();
        ira.setName("Ira");
        ira.setEmail("ira@nu.eli.pali");
        ira.setCreatedAt(OffsetDateTime.now());

        return ira;
    }
}
