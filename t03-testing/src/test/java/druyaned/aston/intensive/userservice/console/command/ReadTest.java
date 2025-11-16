package druyaned.aston.intensive.userservice.console.command;

import static druyaned.aston.intensive.userservice.console.command.CommandUtils.BAD_INPUT;
import druyaned.aston.intensive.userservice.dao.UserDao;
import druyaned.aston.intensive.userservice.model.UserEntity;
import java.time.OffsetDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ReadTest {

    @Mock
    private UserDao userDao;

    private Command command;

    @BeforeEach
    public void setUpTestMethod() {
        command = new Read(userDao);
    }

    @Test
    public void invalidInputLength() {
        String[] input = new String[]{Read.CODE, "one", "two"};
        assertEquals(BAD_INPUT, command.execute(input));

        input = new String[]{Read.CODE};
        assertEquals(BAD_INPUT, command.execute(input));
    }

    @Test
    public void invalidId() {
        String idStr = "not_an_ID";
        String expectedOutput = "Id '" + idStr + "' can't be parsed";

        String[] input = new String[]{Read.CODE, idStr};
        assertEquals(expectedOutput, command.execute(input));
    }

    @Test
    public void noUserByNonExistingId() {
        Long id = 1001L;
        String expectedOutput = "There is no user with id=" + id;

        when(userDao.find(id)).thenReturn(null);

        String[] input = new String[]{Read.CODE, id.toString()};
        assertEquals(expectedOutput, command.execute(input));

        verify(userDao).find(id);
    }

    @Test
    public void readUserByExistingId() {
        UserEntity mia = new UserEntity();
        mia.setId(1001L);
        mia.setName("Mia");
        mia.setEmail("mia@example.com");
        mia.setCreatedAt(OffsetDateTime.parse("2025-05-20t13:00:00+03"));

        String expectedOutput = "Found user: " + CommandUtils.outputUser(mia);

        when(userDao.find(anyLong())).thenReturn(mia);

        String[] input = new String[]{Read.CODE, mia.getId().toString()};
        assertEquals(expectedOutput, command.execute(input));

        verify(userDao).find(mia.getId());
    }
}
