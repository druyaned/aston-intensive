package druyaned.aston.intensive.userservice.console;

import druyaned.aston.intensive.userservice.console.command.Command;
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
    
    private static final Scanner SIN = new Scanner(System.in);
    
    private final Map<String, Command> codeToCommand;
    
    public AppConsole(EntityManager entityManager, Validator validator) {
        codeToCommand = new HashMap<>();
        UserDao userDao = new UserDaoImpl(entityManager);
        putCommand(new HelpMenu());
        putCommand(new Quit());
        putCommand(new Create(userDao, validator));
        putCommand(new ReadAll(userDao, validator));
        putCommand(new Read(userDao, validator));
        putCommand(new Update(userDao, validator));
        putCommand(new Delete(userDao, validator));
    }
    
    private void putCommand(Command command) {
        codeToCommand.put(command.code(), command);
    }
    
    @Override
    public void run() {
        String inputPrompt = "input ('" + HelpMenu.CODE + "' - help): ";
        String[] input;
        try {
            do {
                System.out.print("\n" + inputPrompt);
                input = SIN.nextLine().split(" ");
                commandExecution(input);
            } while (input.length != 1 || !input[0].equals(Quit.CODE));
        } catch (Exception exc) {
            System.err.println("Unexpected exception was caught.");
            exc.printStackTrace();
            System.err.println("Exiting the app...");
        }
    }
    
    private void commandExecution(String[] input) {
        if (input == null || input.length == 0) {
            System.out.println("Bad input");
            return;
        }
        Command command = codeToCommand.get(input[0]);
        if (command == null) {
            System.out.println("Bad input");
        } else {
            System.out.println(command.execute(input));
        }
    }
}
