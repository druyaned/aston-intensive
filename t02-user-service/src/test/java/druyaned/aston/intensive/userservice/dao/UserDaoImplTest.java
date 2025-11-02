package druyaned.aston.intensive.userservice.dao;

import druyaned.aston.intensive.userservice.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserDaoImplTest {
    
    private EntityManagerFactory emf;
    private EntityManager entityManager;
    
    @BeforeEach
    public void setUp() {
        emf = Persistence.createEntityManagerFactory("UserServiceTest");
        entityManager = emf.createEntityManager();
    }
    
    @AfterEach
    public void cleanUp() {
        if (emf.isOpen()) {
            emf.close();
        }
        if (entityManager.isOpen()) {
            entityManager.close();
        }
    }
    
    @Test
    public void saveCorrectUsers() {
        UserDao userDao = new UserDaoImpl(entityManager);
        List<User> found = userDao.findAll();
        assertEquals(0, found.size());
        List<User> data = getData();
        for (User user : data) {
            userDao.save(user);
        }
        found = userDao.findAll();
        assertEquals(data.size(), found.size());
        Set<String> expectedNames = new TreeSet<>();
        Set<String> expectedEmails = new TreeSet<>();
        Set<String> actualNames = new TreeSet<>();
        Set<String> actualEmails = new TreeSet<>();
        for (User user : data) {
            expectedNames.add(user.getName());
            expectedEmails.add(user.getEmail());
        }
        for (User user : found) {
            actualNames.add(user.getName());
            actualEmails.add(user.getEmail());
        }
        assertIterableEquals(expectedNames, actualNames);
        assertIterableEquals(expectedEmails, actualEmails);
    }
    
    @Test
    public void saveIncorrectUser() {
        UserDao userDao = new UserDaoImpl(entityManager);
        User user = new User();
        assertThrows(ConstraintViolationException.class, () ->
                userDao.save(user));
    }
    
    @Test
    public void saveUserWithDuplicateEmail() {
        UserDao userDao = new UserDaoImpl(entityManager);
        User user = new User();
        user.setName("Max");
        user.setEmail("max@example.com");
        ZoneOffset zoneOffset = OffsetDateTime.now().getOffset();
        OffsetDateTime createdAt = OffsetDateTime.of(2025, 9, 18,
                12, 0, 0, 0, zoneOffset);
        user.setCreatedAt(createdAt);
        userDao.save(user);
        User nextUser = new User();
        nextUser.setName("Leo");
        nextUser.setEmail("max@example.com");
        createdAt = createdAt.plusHours(1L);
        nextUser.setCreatedAt(createdAt);
        try {
            userDao.save(nextUser);
        } catch (Exception exc) {
            System.out.println("exc.class=" + exc.getClass());
        }
        assertThrows(
                org.hibernate.exception.ConstraintViolationException.class,
                () -> userDao.save(nextUser)
        );
    }
    
    // TODO: continue
    
    private static List<User> getData() {
        int n = 5;
        String[] names = {"Max", "Leo", "Kai", "Mia", "Ana"};
        String[] emails = {
            "max@example.com",
            "leo@example.com",
            "kai@example.com",
            "mia@example.com",
            "ana@example.com"
        };
        LocalDate[] birthdates = {
            LocalDate.of(2000, 9, 18),
            LocalDate.of(2001, 8, 17),
            LocalDate.of(2002, 7, 16),
            LocalDate.of(2003, 6, 15),
            LocalDate.of(2004, 5, 14)
        };
        ZoneOffset zoneOffset = OffsetDateTime.now().getOffset();
        OffsetDateTime baseCreatedAt = OffsetDateTime.of(2025, 9, 18,
                12, 0, 0, 0, zoneOffset);
        OffsetDateTime[] createdAtArray = {
            baseCreatedAt.plusHours(1L),
            baseCreatedAt.plusHours(2L),
            baseCreatedAt.plusHours(3L),
            baseCreatedAt.plusHours(4L),
            baseCreatedAt.plusHours(5L)
        };
        List<User> users = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            User user = new User();
            user.setName(names[i]);
            user.setEmail(emails[i]);
            user.setBirthdate(birthdates[i]);
            user.setCreatedAt(createdAtArray[i]);
            users.add(user);
        }
        return users;
    }
}
