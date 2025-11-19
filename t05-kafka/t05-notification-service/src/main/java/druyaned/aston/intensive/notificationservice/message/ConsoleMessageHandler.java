package druyaned.aston.intensive.notificationservice.message;

import java.io.PrintStream;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class ConsoleMessageHandler implements MessageHandler {

    private final PrintStream sout;

    public ConsoleMessageHandler(PrintStream printStream) {
        this.sout = printStream;
    }

    @Override
    public void handle(String email, String message) {
        sout.println("[console-handler:" + email + "]: " + message);
    }
}
