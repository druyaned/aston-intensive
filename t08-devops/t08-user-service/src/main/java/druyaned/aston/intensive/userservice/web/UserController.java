package druyaned.aston.intensive.userservice.web;

import druyaned.aston.intensive.userservice.UserServiceApp;
import druyaned.aston.intensive.userservice.model.UserDto;
import druyaned.aston.intensive.userservice.serve.UserService;
import druyaned.aston.intensive.userservice.serve.UserService.Result;
import static druyaned.aston.intensive.userservice.serve.UserService.Result.Type.EMAIL_DUPLICATION;
import static druyaned.aston.intensive.userservice.serve.UserService.Result.Type.FOUND;
import static druyaned.aston.intensive.userservice.serve.UserService.Result.Type.NOT_FOUND;
import static druyaned.aston.intensive.userservice.serve.UserService.Result.Type.UPDATED;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Implementation of CRUD API of {@link UserServiceApp user-service application}.
 *
 * @author druyaned
 */
@RestController
@RequestMapping("/users")
public class UserController implements UserServiceApi {

    private final UserService userService;
    private final UserModelAssembler userModelAssembler;

    public UserController(UserService userService, UserModelAssembler userModelAssembler) {
        this.userService = userService;
        this.userModelAssembler = userModelAssembler;
    }

    @Override
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<UserDto>>> getAll(Pageable pageable) {
        List<UserDto> users = userService.getAll(pageable);

        return ResponseEntity.ok(userModelAssembler.toCollectionModel(users));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UserDto>> get(@PathVariable Long id) {
        Result getResult = userService.get(id);

        return getResult.type() == FOUND
                ? ResponseEntity.ok(userModelAssembler.toModel(getResult.content()))
                : ResponseEntity.notFound().build();
    }

    @Override
    @PostMapping
    public ResponseEntity<Result> create(@Valid @RequestBody UserDto userDto) {
        Result createResult = userService.create(userDto);

        if (createResult.type() == EMAIL_DUPLICATION) {
            return ResponseEntity.badRequest().body(createResult);
        }

        UserDto savedUser = createResult.content();

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(savedUser.getId())
                .toUri();

        return ResponseEntity.created(location).body(createResult);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<Result> update(
            @PathVariable Long id,
            @Valid @RequestBody UserDto userDto) {

        Result updateResult = userService.update(id, userDto);

        return switch (updateResult.type()) {
            case UPDATED -> ResponseEntity.ok(updateResult);
            case NOT_FOUND -> ResponseEntity.notFound().build();
            case EMAIL_DUPLICATION -> ResponseEntity.badRequest().body(updateResult);
            default -> throw new IllegalStateException("Unknown result type");
        };
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Result> delete(@PathVariable Long id) {
        Result deleteResult = userService.delete(id);

        if (deleteResult.type() == NOT_FOUND) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(deleteResult);
    }
}
