package druyaned.aston.intensive.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Step#03: API Gateway service; implementation of the API Gateway pattern. Represents a single
 * entry point for all clients (microservices).
 *
 * @author druyaned
 */
@SpringBootApplication
public class ApiGatewayApp {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApp.class, args);
    }
}
