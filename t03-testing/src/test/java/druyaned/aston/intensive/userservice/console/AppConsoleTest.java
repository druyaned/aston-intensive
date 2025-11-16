package druyaned.aston.intensive.userservice.console;

import static druyaned.aston.intensive.userservice.console.command.CommandUtils.BAD_INPUT;
import druyaned.aston.intensive.userservice.console.command.HelpMenu;
import druyaned.aston.intensive.userservice.console.command.Quit;
import jakarta.persistence.EntityManager;
import jakarta.validation.Validator;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Root test class of the Service layer. Other test classes are located in the
 * {@code command} package. Much more Mockito functionality is presented in the
 * test classes of the {@code command}'s package.
 *
 * <p>
 * <b>Task#03 requirement that are met here</b>:
 * <ol>
 * <li>To test the Service layer, write unit tests using Mockito</li>
 * <li>The tests should be isolated from each other</li>
 * </ol>
 *
 * @author druyaned
 */
public class AppConsoleTest {

    private ByteArrayOutputStream outSource;
    private PrintStream out;
    private PrintStream err;
    private Scanner scanner;

    private EntityManager entityManager;
    private Validator validator;

    @BeforeEach
    public void setUpTestMethod() {
        // Minimal mocks will not be used; avoiding DAO actions
        entityManager = Mockito.mock(EntityManager.class);
        validator = Mockito.mock(Validator.class);

        outSource = new ByteArrayOutputStream();
        out = new PrintStream(outSource);
        err = new PrintStream(new ByteArrayOutputStream());
    }

    @AfterEach
    public void cleanUpTestMethod() {
        err.close();
        out.close();
        entityManager.close();
    }

    @Test
    public void executeHelpAndQuitCommands() {
        String input = String.join(System.lineSeparator(), HelpMenu.CODE,
                Quit.CODE);
        scanner = new Scanner(input);

        AppConsole console = new AppConsole(out, err, scanner, entityManager,
                validator);
        console.run();

        String output = outSource.toString();
        assertTrue(output.contains("Command menu:"), "should print help menu");
        assertTrue(output.contains("Quitting..."), "should print quitting");
    }

    @Test
    public void printBadInputForUndefinedCommand() {
        String input = String.join(System.lineSeparator(), "bad_cmd1",
                "bad_cmd2", Quit.CODE);
        scanner = new Scanner(input);

        AppConsole console = new AppConsole(out, err, scanner, entityManager,
                validator);
        console.run();

        String output = outSource.toString();
        List<String> lines = output.lines().toList();

        int numberOfBadInput = 0;
        for (String line : lines) {
            if (line.contains(BAD_INPUT)) {
                numberOfBadInput++;
            }
        }
        assertEquals(2, numberOfBadInput);
    }
}
