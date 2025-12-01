package druyaned.aston.intensive.apigateway.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Step#07: provides a fallback that is called in case of a unavailability of the user-service.
 *
 * @author druyaned
 */
@RestController
public class FallbackController {

    @GetMapping("/fallback/users")
    public Mono<String> userServiceFallback() {
        return Mono.just("User service is not responding. Maybe, a developer is working on it.");
    }
}
