package druyaned.aston.intensive.userservice.console.command;

import static druyaned.aston.intensive.userservice.console.command.CommandUtils.BAD_INPUT;
import druyaned.aston.intensive.userservice.dao.UserDao;
import druyaned.aston.intensive.userservice.model.UserEntity;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ReadAllTest {

    @Mock
    private UserDao userDao;

    private Command command;

    @BeforeEach
    public void setUpTestMethod() {
        command = new ReadAll(userDao);
    }

    @Test
    public void invalidInputLength() {
        String[] input = new String[]{ReadAll.CODE, "extra"};
        assertEquals(BAD_INPUT, command.execute(input));
    }

    @Test
    public void emptyListOfUsers() {
        when(userDao.findAll()).thenReturn(Collections.emptyList());

        String[] input = new String[]{ReadAll.CODE};
        String output = command.execute(input);
        assertEquals("There are no users yet", output);

        verify(userDao).findAll();
    }

    @Test
    public void nonEmptyListOfUsers() {
        OffsetDateTime createdAt = OffsetDateTime
                .parse("2025-05-20t13:00:00+03");

        UserEntity kai = new UserEntity();
        kai.setId(1L);
        kai.setName("Kai");
        kai.setEmail("kai@example.com");
        kai.setCreatedAt(createdAt);

        UserEntity mia = new UserEntity();
        mia.setId(2L);
        mia.setName("Mia");
        mia.setEmail("mia@example.com");
        mia.setCreatedAt(createdAt);

        when(userDao.findAll()).thenReturn(Arrays.asList(kai, mia));

        String[] input = new String[]{ReadAll.CODE};
        String output = command.execute(input);
        assertTrue(output.contains("All users:"));

        String kaiPart = String.format("id=%d name=%s email=%s createdAt=%s",
                kai.getId(), kai.getName(), kai.getEmail(), kai.getCreatedAt());
        String miaPart = String.format("id=%d name=%s email=%s createdAt=%s",
                mia.getId(), mia.getName(), mia.getEmail(), mia.getCreatedAt());

        assertTrue(output.contains(kaiPart));
        assertTrue(output.contains(miaPart));

        verify(userDao).findAll();
    }
}
