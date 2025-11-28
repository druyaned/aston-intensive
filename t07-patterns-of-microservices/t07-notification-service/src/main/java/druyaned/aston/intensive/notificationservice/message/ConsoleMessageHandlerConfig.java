package druyaned.aston.intensive.notificationservice.message;

import java.io.PrintStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Provides {@link PrintStream} for {@link ConsoleMessageHandler}.
 *
 * @author druyaned
 */
@Configuration
public class ConsoleMessageHandlerConfig {

    @Bean
    public PrintStream printStream() {
        return System.out;
    }
}
