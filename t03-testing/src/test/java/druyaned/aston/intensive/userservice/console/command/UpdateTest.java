package druyaned.aston.intensive.userservice.console.command;

import static druyaned.aston.intensive.userservice.console.command.CommandUtils.BAD_INPUT;
import druyaned.aston.intensive.userservice.dao.UserDao;
import druyaned.aston.intensive.userservice.model.UserEntity;
import jakarta.validation.Validator;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UpdateTest {

    @Mock
    private UserDao userDao;
    @Mock
    private Validator validator;

    private Command command;

    @BeforeEach
    public void setUpTestMethod() {
        command = new Update(userDao, validator);
    }

    @Test
    public void invalidInputLength() {
        String[] input = new String[]{Update.CODE, "one", "two"};
        assertEquals(BAD_INPUT, command.execute(input));
    }

    @Test
    public void invalidId() {
        String idStr = "invalid_ID";
        String name = "Name";
        String email = "email@example.com";
        String birthdate = "-";

        String expectedOutput = "Id '" + idStr + "' can't be parsed";

        String[] input = new String[]{Update.CODE, idStr, name, email,
            birthdate};
        assertEquals(expectedOutput, command.execute(input));
    }

    @Test
    public void invalidBirthdate() {
        Long id = 1001L;
        String name = "Name";
        String email = "email@example.com";
        String birthdate = "20-20-20";

        String expectedOutput = "Birthdate '" + birthdate + "' can't be parsed";

        String[] input = new String[]{Update.CODE, id.toString(), name, email,
            birthdate};
        assertEquals(expectedOutput, command.execute(input));
    }

    @Test
    public void userNotFound() {
        Long id = 1001L;
        String name = "Name";
        String email = "email@example.com";
        String birthdate = "2000-01-01";

        when(validator.validate(any(UserEntity.class)))
                .thenReturn(Collections.emptySet());
        when(userDao.find(id)).thenReturn(null);

        String expectedOutput = "There is no user with id=" + id;

        String[] input = new String[]{Update.CODE, id.toString(), name, email,
            birthdate};
        assertEquals(expectedOutput, command.execute(input));

        verify(validator).validate(any(UserEntity.class));
        verify(userDao).find(id);
    }

    @Test
    public void notEqualAndExistingEmail() {
        String newEmail = "kai@example.com";
        String birthdateStr = "2000-01-01";

        UserEntity mia = new UserEntity();
        mia.setId(1001L);
        mia.setName("Mia");
        mia.setEmail("mia@example.com");
        mia.setBirthdate(LocalDate.parse(birthdateStr));
        mia.setCreatedAt(OffsetDateTime.parse("2025-05-20t13:00:00+03"));

        when(validator.validate(any(UserEntity.class)))
                .thenReturn(Collections.emptySet());
        when(userDao.find(mia.getId())).thenReturn(mia);
        when(userDao.emailExists(newEmail)).thenReturn(true);

        String expectedOutput = "Email '" + newEmail + "' already exists";

        String[] input = new String[]{Update.CODE, mia.getId().toString(),
            mia.getName(), newEmail, birthdateStr};
        assertEquals(expectedOutput, command.execute(input));

        verify(validator).validate(any(UserEntity.class));
        verify(userDao).find(mia.getId());
        verify(userDao).emailExists(newEmail);
    }

    @Test
    public void updateCorrectly() {
        String newName = "Mary";
        String newEmail = "mary@example.com";
        String birthdateStr = "2000-01-01";

        UserEntity mia = new UserEntity();
        mia.setId(1001L);
        mia.setName("Mia");
        mia.setEmail("mia@example.com");
        mia.setBirthdate(LocalDate.parse(birthdateStr));
        mia.setCreatedAt(OffsetDateTime.parse("2025-05-20t13:00:00+03"));

        UserEntity updatedUser = new UserEntity();
        updatedUser.setId(1001L);
        updatedUser.setName(newName);
        updatedUser.setEmail(newEmail);
        updatedUser.setBirthdate(mia.getBirthdate());
        updatedUser.setCreatedAt(mia.getCreatedAt());

        when(validator.validate(any(UserEntity.class)))
                .thenReturn(Collections.emptySet());
        when(userDao.find(mia.getId())).thenReturn(mia);
        when(userDao.emailExists(newEmail)).thenReturn(false);

        String expectedOutput = "Updated: "
                + CommandUtils.outputUser(updatedUser);

        String[] input = new String[]{Update.CODE, mia.getId().toString(),
            newName, newEmail, birthdateStr};
        assertEquals(expectedOutput, command.execute(input));

        verify(validator).validate(any(UserEntity.class));
        verify(userDao).find(mia.getId());
        verify(userDao).emailExists(newEmail);
        verify(userDao).update(any(UserEntity.class));
    }
}
