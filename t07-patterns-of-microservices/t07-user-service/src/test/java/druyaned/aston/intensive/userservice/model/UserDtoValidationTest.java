package druyaned.aston.intensive.userservice.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Validation unit-tests for {@link UserDto}.
 *
 * @author druyaned
 */
public class UserDtoValidationTest {

    private static Validator validator;

    private UserDto userDto;

    @BeforeAll
    public static void setUpTestClass() {
        try (ValidatorFactory vf = Validation.buildDefaultValidatorFactory()) {
            validator = vf.getValidator();
        }
    }

    @BeforeEach
    public void setUpTestMethod() {
        userDto = new UserDto();

        userDto.setId(1L);
        userDto.setName("Kai");
        userDto.setEmail("kai@boombox.org");
        userDto.setBirthdate(LocalDate.parse("2002-07-16"));
        userDto.setCreatedAt(OffsetDateTime.parse("2025-10-26T03:30:43.559921+03"));
    }

    @Test
    public void nullNameIsInvalid() {
        userDto.setName(null);

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        assertEquals(1, violations.size());

        ConstraintViolation<UserDto> violation = violations.iterator().next();

        assertEquals("name", violation.getPropertyPath().toString());
        assertEquals("Name must not be blank", violation.getMessage());
    }

    @Test
    public void nameFromOneLetterIsInvalid() {
        userDto.setName("A");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        assertEquals(1, violations.size());

        ConstraintViolation<UserDto> violation = violations.iterator().next();

        assertEquals("name", violation.getPropertyPath().toString());
    }

    @Test
    public void tooBigNameIsInvalid() {
        userDto.setName("a".repeat(128));

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        assertEquals(1, violations.size());

        ConstraintViolation<UserDto> violation = violations.iterator().next();

        assertEquals("name", violation.getPropertyPath().toString());
    }

    @Test
    public void validName() {
        userDto.setName("Kai");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @MethodSource("makeInvalidEmails")
    public void invalidEmail(String email) {
        userDto.setEmail(email);

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        assertEquals(1, violations.size());

        ConstraintViolation<UserDto> violation = violations.iterator().next();

        assertEquals("email", violation.getPropertyPath().toString());
    }

    public static Stream<String> makeInvalidEmails() {
        return Stream.of(
                "",                             // empty
                "a".repeat(65) + "@abcd.com",   // local is too big
                "a@" + "a".repeat(255) + ".com",// domain is too big
                "plainaddress",                 // no @
                "@domain.com",                  // no local part
                "user@",                        // no domain
                "user@-domain.com",             // domain starts with '-'
                "user@domain",                  // no TLD
                "user@domain.c",                // TLD too short
                "user..name@domain.com",        // double dot in local part
                ".username@domain.com",         // local starts with dot
                "user.@domain.com",             // local ends with dot
                "user@do..main.com",            // double dot in domain
                "user@domain.c_m",              // underscore in TLD
                "user@do main.com",             // space
                "user@domain..com",             // double dot before TLD
                "us er@domain.com",             // space in local part
                "user@.domain.com",             // domain starts with dot
                "user@domain.com.");            // trailing dot
    }

    @ParameterizedTest
    @MethodSource("makeValidEmails")
    public void validEmail(String email) {
        userDto.setEmail(email);

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        assertTrue(violations.isEmpty());
    }

    public static Stream<String> makeValidEmails() {
        return Stream.of(
                "a@b.com",
                "user@domain.com",
                "USER_123-xyz@sub.domain.org",
                "user.name@domain.co",
                "user_name@sub-domain.example.net",
                "u1.u2_u3-4@very-long-domain-name-example.com",
                "a_b-c.d_e-f@domain.co.uk");
    }
}
