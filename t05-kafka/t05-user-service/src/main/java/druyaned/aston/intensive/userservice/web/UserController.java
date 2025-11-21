package druyaned.aston.intensive.userservice.web;

import druyaned.aston.intensive.userservice.model.UserDto;
import druyaned.aston.intensive.userservice.notify.UserEvent;
import druyaned.aston.intensive.userservice.serve.UserService;
import druyaned.aston.intensive.userservice.serve.UserService.Result;
import static druyaned.aston.intensive.userservice.serve.UserService.Result.Type.EMAIL_DUPLICATION;
import static druyaned.aston.intensive.userservice.serve.UserService.Result.Type.FOUND;
import static druyaned.aston.intensive.userservice.serve.UserService.Result.Type.NOT_FOUND;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/user-service")
public class UserController {

    private final UserService userService;
    private final KafkaTemplate<String, UserEvent> kafkaTemplate;
    private final String userEventsTopic;

    public UserController(UserService userService, KafkaTemplate<String, UserEvent> kafkaTemplate,
            @Value("${topics.userEvents.name}") String userEventsTopic) {

        this.userService = userService;
        this.kafkaTemplate = kafkaTemplate;
        this.userEventsTopic = userEventsTopic;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAll(Pageable pageable) {
        return ResponseEntity.ok(userService.getAll(pageable).content());
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<UserDto> get(@PathVariable Long id) {
        Result<UserDto> result = userService.get(id);

        return result.type() == FOUND
                ? ResponseEntity.ok(result.content())
                : ResponseEntity.notFound().build();
    }

    @PostMapping("/create")
    public ResponseEntity<String> create(
            // @Valid invokes the validation and if it fails, MethodArgumentNotValidException
            // is thrown. By default Spring translates MethodArgumentNotValidException into bad
            // request (HTTP 400)
            @Valid @RequestBody UserDto userDto) {

        Result<UserDto> result = userService.create(userDto);

        if (result.type() == EMAIL_DUPLICATION) {
            return ResponseEntity.badRequest().body(result.message());
        }

        UserDto savedUser = result.content();

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/user")
                .path("/{id}")
                .buildAndExpand(savedUser.getId())
                .toUri();

        kafkaTemplate.send(userEventsTopic, savedUser.getEmail(),
                new UserEvent(UserEvent.Type.CREATE, savedUser.getId()));

        return ResponseEntity.created(location).body(result.message());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> update(@PathVariable Long id,
            @Valid @RequestBody UserDto userDto) {

        Result<UserDto> result = userService.update(id, userDto);

        return switch (result.type()) {
            case UPDATED -> ResponseEntity.ok(result.message());
            case NOT_FOUND -> ResponseEntity.notFound().build();
            case EMAIL_DUPLICATION -> ResponseEntity.badRequest().body(result.message());
            default -> throw new IllegalStateException("Unknown result type");
        };
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<String> delete(@PathVariable Long id) {
        Result<UserDto> result = userService.delete(id);

        if (result.type() == NOT_FOUND) {
            return ResponseEntity.notFound().build();
        }

        kafkaTemplate.send(userEventsTopic, result.content().getEmail(),
                new UserEvent(UserEvent.Type.DELETE, result.content().getId()));

        return ResponseEntity.ok(result.message());
    }
}
