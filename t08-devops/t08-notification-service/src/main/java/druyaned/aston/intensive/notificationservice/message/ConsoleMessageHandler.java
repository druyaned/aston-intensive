package druyaned.aston.intensive.notificationservice.message;

import java.io.PrintStream;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * {@link MessageHandler} of {@link PrintStream}.
 *
 * @author druyaned
 */
@Component
@Primary
public class ConsoleMessageHandler implements MessageHandler {

    private final PrintStream sout;

    public ConsoleMessageHandler(PrintStream printStream) {
        this.sout = printStream;
    }

    /**
     * Prints the message to the {@link PrintStream}.
     *
     * @param email recipient
     * @param message to be printed
     */
    @Override
    public void handle(String email, String message) {
        sout.println("[console-handler:" + email + "]: " + message);
    }
}
