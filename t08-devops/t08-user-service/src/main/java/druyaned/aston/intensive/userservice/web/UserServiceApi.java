package druyaned.aston.intensive.userservice.web;

import druyaned.aston.intensive.userservice.model.UserDto;
import druyaned.aston.intensive.userservice.serve.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;

/**
 * Prep#01: CRUD API of {@link UserServiceApp user-service application}.
 *
 * @author druyaned
 *
 * @see UserController
 */
@Tag(name = "Users", description = "User management API")
public interface UserServiceApi {

    @Operation(summary = "Get all users",
            description = "Returns a paged list of users with HATEOAS links")
    @ApiResponse(responseCode = "200", description = "Users are found (possibly empty list)")
    ResponseEntity<CollectionModel<EntityModel<UserDto>>> getAll(
            @Parameter(description = "Pagination parameters") Pageable pageable);

    @Operation(summary = "Get user by ID", description = "Returns single user with HATEOAS links")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User is found"),
        @ApiResponse(responseCode = "404", description = "User is not found")
    })
    ResponseEntity<EntityModel<UserDto>> get(
            @Parameter(description = "User ID", required = true) Long id);

    @Operation(summary = "Create a new user")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "User is created"),
        @ApiResponse(responseCode = "400", description = "Email duplication or validation error")
    })
    ResponseEntity<UserService.Result> create(
            @Parameter(description = "User Data Transfer Object", required = true) UserDto userDto);

    @Operation(summary = "Update existing user")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User is updated"),
        @ApiResponse(responseCode = "400", description = "Email duplication or validation error"),
        @ApiResponse(responseCode = "404", description = "User is not found")
    })
    ResponseEntity<UserService.Result> update(
            @Parameter(description = "User ID", required = true) Long id,
            @Parameter(description = "User Data Transfer Object", required = true) UserDto userDto);

    @Operation(summary = "Delete existing user")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User is deleted"),
        @ApiResponse(responseCode = "404", description = "User is not found")
    })
    ResponseEntity<UserService.Result> delete(
            @Parameter(description = "User ID", required = true) Long id);
}
