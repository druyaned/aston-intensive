package druyaned.aston.intensive.userservice.console.command;

import druyaned.aston.intensive.userservice.console.AppConsole;

/**
 * Command to be used in the {@link AppConsole console app},
 * a usage example of {@code Command Design Pattern}; provides {@link code}
 * (to be a key in HashMap) and {@link execute} functionality.
 * 
 * <p>
 * <b>Task requirement:</b>: "Handle possible exceptions related to Hibernate
 * and PostgreSQL". I've decided to give all responsibilities to the commands,
 * not to the invoker ({@link AppConsole}). So all possible exceptions that
 * are related to the execution are handled, not rethrown.
 * 
 * @author druyaned
 */
public interface Command {
    
    /**
     * Returns a code of the command which must contain only lower case
     * english letters and be quite short (for usage in HashMap and
     * console interface).
     * 
     * @return concise code of the command
     */
    String code();
    
    /**
     * Execution of the command which accepts the given input arguments from
     * the app user and returns some output message or data as a string.
     * 
     * @param input arguments from the app user
     * @return some output message or data
     */
    String execute(String[] input);
}
