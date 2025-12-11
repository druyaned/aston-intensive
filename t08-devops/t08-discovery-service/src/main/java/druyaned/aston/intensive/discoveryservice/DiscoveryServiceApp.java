package druyaned.aston.intensive.discoveryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Discovery Service (a.k.a Eureka Server); implementation of the pattern "Server-side service
 * discovery". Service Registry (Eureka) is basically a phonebook for microservices, with
 * auto-updates.
 *
 * @author druyaned
 */
@SpringBootApplication
@EnableEurekaServer
public class DiscoveryServiceApp {

    public static void main(String[] args) {
        SpringApplication.run(DiscoveryServiceApp.class, args);
    }
}
