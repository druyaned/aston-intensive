package druyaned.aston.intensive.userservice.console;

import druyaned.aston.intensive.userservice.console.command.Command;
import static druyaned.aston.intensive.userservice.console.command.CommandUtils.BAD_INPUT;
import druyaned.aston.intensive.userservice.console.command.Create;
import druyaned.aston.intensive.userservice.console.command.Delete;
import druyaned.aston.intensive.userservice.console.command.HelpMenu;
import druyaned.aston.intensive.userservice.console.command.Quit;
import druyaned.aston.intensive.userservice.console.command.Read;
import druyaned.aston.intensive.userservice.console.command.ReadAll;
import druyaned.aston.intensive.userservice.console.command.Update;
import druyaned.aston.intensive.userservice.dao.UserDao;
import druyaned.aston.intensive.userservice.dao.UserDaoImpl;
import jakarta.persistence.EntityManager;
import jakarta.validation.Validator;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Invoker of and main director of the app. It holds command map
 * and requests input from an app user by {@link System#in}.
 *
 * <p>
 * <b>Task requirement</b>: "Use the console interface to interact with
 * the user". It's met by this class and {@link Command}.
 *
 * @author druyaned
 */
public class AppConsole implements Runnable {

    private final PrintStream out;
    private final PrintStream err;
    private final Scanner scanner;
    private final Map<String, Command> codeToCommand;

    /**
     * Constructs an instance of AppConsole to be {@link run() run}.
     *
     * <p>
     * P.S. I could use Builder pattern to avoid parameter overloading, but the
     * purpose of the design patterns to simplify design and code, not to
     * clutter them up.
     *
     * @param out to print output of the commands
     * @param err to print some error report
     * @param scanner to interact with the user and execute commands
     *
     * @param entityManager to communicate with a database
     * @param validator to perform validation of entities
     */
    public AppConsole(PrintStream out, PrintStream err, Scanner scanner,
            EntityManager entityManager, Validator validator) {

        this.out = out;
        this.err = err;
        this.scanner = scanner;

        codeToCommand = new HashMap<>();
        UserDao userDao = new UserDaoImpl(entityManager);
        putCommand(new HelpMenu());
        putCommand(new Quit());
        putCommand(new Create(userDao, validator));
        putCommand(new ReadAll(userDao));
        putCommand(new Read(userDao));
        putCommand(new Update(userDao, validator));
        putCommand(new Delete(userDao));
    }

    private void putCommand(Command command) {
        codeToCommand.put(command.code(), command);
    }

    /**
     * Runs an interactive console app. It requests some input that consists of
     * command code and arguments. There are some special commands, e.g. help
     * menu, so it's easy to figure out.
     */
    @Override
    public void run() {
        String inputPrompt = "input ('" + HelpMenu.CODE + "' - help): ";
        String[] input;

        try {
            do {
                out.print("\n" + inputPrompt);
                input = scanner.nextLine().split(" ");
                commandExecution(input);

            } while (input.length != 1 || !input[0].equals(Quit.CODE));

        } catch (Exception exc) {
            err.println("Unexpected exception was caught.");
            err.println("Exception message: " + exc.getMessage());

            err.println("Stack trace:");
            for (StackTraceElement element : exc.getStackTrace()) {
                err.println("  " + element);
            }

            err.println("Ending app execution...");
        }
    }

    private void commandExecution(String[] input) {
        if (input == null || input.length == 0) {
            out.println(BAD_INPUT);
            return;
        }

        String commandCode = input[0];
        out.println(codeToCommand.containsKey(commandCode)
                ? codeToCommand.get(commandCode).execute(input)
                : BAD_INPUT);
    }
}
