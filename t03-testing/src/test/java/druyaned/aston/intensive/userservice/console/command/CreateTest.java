package druyaned.aston.intensive.userservice.console.command;

import static druyaned.aston.intensive.userservice.console.command.CommandUtils.BAD_INPUT;
import druyaned.aston.intensive.userservice.dao.UserDao;
import druyaned.aston.intensive.userservice.model.UserEntity;
import jakarta.validation.Validator;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CreateTest {

    @Mock
    private UserDao userDao;
    @Mock
    private Validator validator;

    private Command command;

    @BeforeEach
    public void setUpTestMethod() {
        command = new Create(userDao, validator);
    }

    @Test
    public void invalidInputLength() {
        String[] input = new String[]{Create.CODE, "one", "two"};
        assertEquals(BAD_INPUT, command.execute(input));
    }

    @Test
    public void duplicateEmail() {
        String name = "Name";
        String email = "mail@example.com";
        String birthdate = "2000-01-01";

        when(userDao.emailExists(email)).thenReturn(true);

        String[] input = new String[]{Create.CODE, name, email, birthdate};
        String output = command.execute(input);
        assertEquals("Email '" + email + "' already exists", output);

        verify(userDao).emailExists(email);
    }

    @Test
    public void invalidBirthdate() {
        String name = "Name";
        String email = "mail@example.com";
        String birthdate = "20-20-20";

        when(userDao.emailExists(email)).thenReturn(false);

        String[] input = new String[]{Create.CODE, name, email, birthdate};
        String output = command.execute(input);
        assertEquals("Birthdate '" + birthdate + "' can't be parsed", output);

        verify(userDao).emailExists(email);
    }

    @Test
    public void correctInput() {
        String name = "Name";
        String email = "mail@example.com";
        String birthdate = "2000-01-01";

        when(userDao.emailExists(email)).thenReturn(false);
        when(validator.validate(any(UserEntity.class)))
                .thenReturn(Collections.emptySet());

        String[] input = new String[]{Create.CODE, name, email, birthdate};
        String output = command.execute(input);
        assertTrue(output.contains("Created:"));

        verify(userDao).emailExists(email);
        verify(userDao).save(any(UserEntity.class));
        verify(validator).validate(any(UserEntity.class));
    }
}
