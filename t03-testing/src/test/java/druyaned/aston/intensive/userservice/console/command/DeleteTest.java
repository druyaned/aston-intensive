package druyaned.aston.intensive.userservice.console.command;

import static druyaned.aston.intensive.userservice.console.command.CommandUtils.BAD_INPUT;
import druyaned.aston.intensive.userservice.dao.UserDao;
import druyaned.aston.intensive.userservice.model.UserEntity;
import java.time.LocalDate;
import java.time.OffsetDateTime;
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
public class DeleteTest {

    @Mock
    private UserDao userDao;

    private Command command;

    @BeforeEach
    public void setUpTestMethod() {
        command = new Delete(userDao);
    }

    @Test
    public void invalidInputLength() {
        String[] input = new String[]{Update.CODE, "one", "two"};
        assertEquals(BAD_INPUT, command.execute(input));
    }

    @Test
    public void invalidId() {
        String idStr = "invalid_ID";
        String expectedOutput = "Id '" + idStr + "' can't be parsed";

        String[] input = new String[]{Delete.CODE, idStr};
        assertEquals(expectedOutput, command.execute(input));
    }

    @Test
    public void userNotFound() {
        Long id = 1001L;

        when(userDao.find(id)).thenReturn(null);

        String expectedOutput = "There is no user with id=" + id;

        String[] input = new String[]{Delete.CODE, id.toString()};
        assertEquals(expectedOutput, command.execute(input));

        verify(userDao).find(id);
    }

    @Test
    public void deleteCorrectly() {
        Long id = 1001L;

        UserEntity mia = new UserEntity();
        mia.setId(1001L);
        mia.setName("Mia");
        mia.setEmail("mia@example.com");
        mia.setBirthdate(LocalDate.parse("2000-01-01"));
        mia.setCreatedAt(OffsetDateTime.parse("2025-05-20t13:00:00+03"));

        when(userDao.find(id)).thenReturn(mia);

        String expectedOutput = "Deleted by id=" + id;

        String[] input = new String[]{Delete.CODE, id.toString()};
        assertEquals(expectedOutput, command.execute(input));

        verify(userDao).find(id);
        verify(userDao).delete(any(UserEntity.class));
    }
}
