package druyaned.aston.intensive.userservice.web;

import druyaned.aston.intensive.userservice.model.UserDto;
import druyaned.aston.intensive.userservice.serve.UserService;
import druyaned.aston.intensive.userservice.serve.UserService.Result;
import static druyaned.aston.intensive.userservice.serve.UserService.Result.Type.EMAIL_DUPLICATION;
import static druyaned.aston.intensive.userservice.serve.UserService.Result.Type.FOUND;
import static druyaned.aston.intensive.userservice.serve.UserService.Result.Type.NOT_FOUND;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
 * Steps 4 and 7: swagger-annotations and userModelAssembler.
 *
 * <p>
 * <ol>
 * <li>Step#04: applying swagger-annotations to each method of the controller; annotations:
 * {@link Tag}, {@link Operation}, {@link ApiResponse}, {@link ApiResponses}, {@link Parameter}.
 * Other annotations would be redundant. For example, {@code @RequestBody UserDto userDto} -
 * Springdoc sees {@code @RequestBody} and automatically generates description, schema,
 * required=true, validation rules. Very convenient!</li>
 * <li>Step#07: userModelAssembler bean is added; {@code getAll} and {@code get} methods are changed
 * to return response entities of CollectionModel and EntityModel respectively. I don't touch
 * {@code create}, {@code update} and {@code delete} methods here cause it's too excessive.
 * Navigation through resources is mostly about reading and traversing.</li>
 * </ol>
 *
 * @author druyaned
 */
@RestController
@RequestMapping("/user-service")
@Tag(name = "Users", description = "User management API")
public class UserController {

    private final UserService userService;
    private final UserModelAssembler userModelAssembler;

    public UserController(UserService userService, UserModelAssembler userModelAssembler) {
        this.userService = userService;
        this.userModelAssembler = userModelAssembler;
    }

    @GetMapping("/users")
    @Operation(summary = "Get all users",
            description = "Returns a paged list of users with HATEOAS links")
    @ApiResponse(responseCode = "200", description = "Users are found (possibly empty list)")
    public ResponseEntity<CollectionModel<EntityModel<UserDto>>> getAll(
            @Parameter(description = "Pagination parameters") Pageable pageable) {

        List<UserDto> users = userService.getAll(pageable);

        return ResponseEntity.ok(userModelAssembler.toCollectionModel(users));
    }

    @GetMapping("/user/{id}")
    @Operation(summary = "Get user by ID", description = "Returns single user with HATEOAS links")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User is found"),
        @ApiResponse(responseCode = "404", description = "User is not found")
    })
    public ResponseEntity<EntityModel<UserDto>> get(
            @Parameter(description = "User ID", required = true) @PathVariable Long id) {

        Result getResult = userService.get(id);

        return getResult.type() == FOUND
                ? ResponseEntity.ok(userModelAssembler.toModel(getResult.content()))
                : ResponseEntity.notFound().build();
    }

    @PostMapping("/create")
    @Operation(summary = "Create a new user")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "User is created"),
        @ApiResponse(responseCode = "400", description = "Email duplication or validation error")
    })
    public ResponseEntity<String> create(@Valid @RequestBody UserDto userDto) {

        Result createResult = userService.create(userDto);

        if (createResult.type() == EMAIL_DUPLICATION) {
            return ResponseEntity.badRequest().body(createResult.message());
        }

        UserDto savedUser = createResult.content();

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/user")
                .path("/{id}")
                .buildAndExpand(savedUser.getId())
                .toUri();

        return ResponseEntity.created(location).body(createResult.message());
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Update existing user")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User is updated"),
        @ApiResponse(responseCode = "400", description = "Email duplication or validation error"),
        @ApiResponse(responseCode = "404", description = "User is not found")
    })
    public ResponseEntity<String> update(
            @Parameter(description = "User ID", required = true) @PathVariable Long id,
            @Valid @RequestBody UserDto userDto) {

        Result updateResult = userService.update(id, userDto);

        return switch (updateResult.type()) {
            case UPDATED -> ResponseEntity.ok(updateResult.message());
            case NOT_FOUND -> ResponseEntity.notFound().build();
            case EMAIL_DUPLICATION -> ResponseEntity.badRequest().body(updateResult.message());
            default -> throw new IllegalStateException("Unknown result type");
        };
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete existing user")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User is deleted"),
        @ApiResponse(responseCode = "404", description = "User is not found")
    })
    public ResponseEntity<String> delete(@PathVariable Long id) {
        Result deleteResult = userService.delete(id);

        if (deleteResult.type() == NOT_FOUND) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(deleteResult.message());
    }
}
