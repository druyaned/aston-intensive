package druyaned.aston.intensive.configservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Step#10: Config Service (a.k.a Config Server); implementation of the patterns "Externalized
 * Configuration". Provides configurations of most of the services in the system.
 *
 * @author druyaned
 */
@SpringBootApplication
@EnableConfigServer
public class ConfigServiceApp {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServiceApp.class, args);
    }
}
