package druyaned.aston.intensive.userservice.console.command;

import druyaned.aston.intensive.userservice.console.AppConsole;

/**
 * Prints some quitting message and serves {@link AppConsole invoker} to stop
 * the execution of the app.
 *
 * @author druyaned
 */
public class Quit implements Command {

    public static final String CODE = "q";

    public static final String DESCRIPTION = "'" + CODE + "' - quit";

    @Override
    public String code() {
        return CODE;
    }

    @Override
    public String execute(String[] input) {
        String violationMessage = CommandUtils.verifyInput(input, 1);
        if (violationMessage != null) {
            return violationMessage;
        }

        return "Quitting...";
    }
}
