package druyaned.aston.intensive.userservice.web;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This configuration class provides a simple {@link OpenAPI OpenAPI bean}.
 *
 * @author druyaned
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI userServiceOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("User Service API")
                .version("1.0")
                .description("CRUD API of users"));
    }
}
