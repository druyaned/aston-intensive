package druyaned.aston.intensive.userservice.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserValidationTest {
    
    private Validator validator;
    private ZoneOffset offset;
    private User user;
    
    @BeforeEach
    public void setUp() {
        ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
        validator = vf.getValidator();
        offset = OffsetDateTime.now().getOffset();
        user = new User();
        user.setId(1L);
        user.setName("Grisha");
        user.setEmail("grisha@example.com");
        user.setBirthdate(LocalDate.of(2000, 5, 20));
        user.setCreatedAt(OffsetDateTime.of(2025, 9, 18, 12, 0, 0, 0, offset));
    }
    
    @Test public void testIdValidation() {
        user.setId(null);
        assertTrue(validator.validate(user).isEmpty());
        user.setId(Long.MAX_VALUE);
        assertTrue(validator.validate(user).isEmpty());
        user.setId(Long.MIN_VALUE);
        assertTrue(validator.validate(user).isEmpty());
    }
    
    @Test public void testNameValidation() {
        // Good names
        user.setName("Vladimir✺");
        assertTrue(validator.validate(user).isEmpty());
        user.setName("Elisei");
        assertTrue(validator.validate(user).isEmpty());
        // Bad names
        user.setName("A");
        assertFalse(validator.validate(user).isEmpty());
        user.setName("✺");
        assertFalse(validator.validate(user).isEmpty());
        user.setName("b".repeat(128));
        assertFalse(validator.validate(user).isEmpty());
        user.setName("");
        assertFalse(validator.validate(user).isEmpty());
        user.setName(null);
        assertFalse(validator.validate(user).isEmpty());
    }
    
    @Test public void testEmailValidation() {
        List<String> goodEmails = new ArrayList<>();
        goodEmails.add("good@email.com");
        goodEmails.add(
                "_aaaaaaa.0001111-ccccccc.2223333"
                + ".aaaaaaa.0001111.ccccccc.2223333@"
                + "aaaaaaaa.0001111.ccccccc.2223333"
                + ".aaaaaaa.0001111.ccccccc.2223333"
                + ".aaaaaaa.0001111.ccccccc.2223333"
                + ".aaaaaaa.0001111.ccccccc.2223333"
                + ".aaaaaaa.0001111.ccccccc.2223333"
                + ".aaaaaaa.0001111.ccccccc.2223333"
                + ".aaaaaaa.0001111.ccccccc.2223333"
                + ".aaaaaaa.0001111.ccccccc.22.com"
        );
        List<String> badEmails = new ArrayList<>();
        badEmails.add(null);
        badEmails.add(
                "_aaaaaaa.0001111-ccccccc.22233339"
                + ".aaaaaaa.0001111.ccccccc.2223333@"
                + "aaaaaaaa.0001111.ccccccc.2223333"
                + ".aaaaaaa.0001111.ccccccc.2223333"
                + ".aaaaaaa.0001111.ccccccc.2223333"
                + ".aaaaaaa.0001111.ccccccc.2223333"
                + ".aaaaaaa.0001111.ccccccc.2223333"
                + ".aaaaaaa.0001111.ccccccc.2223333"
                + ".aaaaaaa.0001111.ccccccc.2223333"
                + ".aaaaaaa.0001111.ccccccc.22.com"
        );
        badEmails.add(
                "_aaaaaa@"
                + "aaaaaaaa.0001111.ccccccc.2223333"
                + ".aaaaaaa.0001111.ccccccc.2223333"
                + ".aaaaaaa.0001111.ccccccc.2223333"
                + ".aaaaaaa.0001111.ccccccc.2223333"
                + ".aaaaaaa.0001111.ccccccc.2223333"
                + ".aaaaaaa.0001111.ccccccc.2223333"
                + ".aaaaaaa.0001111.ccccccc.22233339"
                + ".aaaaaaa.0001111.ccccccc.22.com"
        );
        badEmails.add("a2223333@ccccc.22co.m");
        for (String email : goodEmails) {
            user.setEmail(email);
            assertTrue(validator.validate(user).isEmpty());
        }
        for (String email : badEmails) {
            user.setEmail(email);
            assertFalse(validator.validate(user).isEmpty());
        }
    }
    
    @Test public void testBirthdateValidation() {
        // Good birthdates
        user.setBirthdate(LocalDate.of(2000, 5, 20));
        assertTrue(validator.validate(user).isEmpty());
        user.setBirthdate(LocalDate.now().minusDays(1L));
        assertTrue(validator.validate(user).isEmpty());
        user.setBirthdate(null);
        assertTrue(validator.validate(user).isEmpty());
        // Bad birthdates
        user.setBirthdate(LocalDate.now());
        assertFalse(validator.validate(user).isEmpty());
        user.setBirthdate(LocalDate.now().plusDays(1L));
        assertFalse(validator.validate(user).isEmpty());
    }
    
    @Test public void testCreatedAtValidation() {
        // Good createdAt fields
        user.setCreatedAt(OffsetDateTime.of(2025, 9, 18, 12, 0, 0, 0, offset));
        assertTrue(validator.validate(user).isEmpty());
        user.setCreatedAt(OffsetDateTime.now());
        assertTrue(validator.validate(user).isEmpty());
        user.setCreatedAt(null);
        assertTrue(validator.validate(user).isEmpty());
        // Bad createdAt fields
        user.setCreatedAt(OffsetDateTime.now().plusDays(1L));
        assertFalse(validator.validate(user).isEmpty());
    }
}
