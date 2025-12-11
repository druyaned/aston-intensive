package druyaned.aston.intensive.userservice.repo;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Provides database connection properties.
 *
 * @author druyaned
 */
@Configuration
@PropertySource("classpath:/db-connection.properties")
public class DbConnectionConfig {}
