package druyaned.aston.intensive.userservice.web;

import druyaned.aston.intensive.userservice.model.UserAdapters;
import druyaned.aston.intensive.userservice.model.UserDto;
import druyaned.aston.intensive.userservice.model.UserEntity;
import druyaned.aston.intensive.userservice.repo.UserRepository;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    private final UserRepository userRepo;

    public UserController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAll(Pageable pageable) {
        Page<UserEntity> page = userRepo.findAll(pageable);

        List<UserDto> userList = page.stream().collect(
                ArrayList::new,
                (list, user) -> list.add(UserAdapters.dtoFromEntity(user)),
                ArrayList::addAll);

        return ResponseEntity.ok(userList);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<UserDto> get(@PathVariable Long id) {
        Optional<UserEntity> userOpt = userRepo.findById(id);

        return userOpt.isPresent()
                ? ResponseEntity.ok(UserAdapters.dtoFromEntity(userOpt.get()))
                : ResponseEntity.notFound().build();
    }

    @PostMapping("/create")
    public ResponseEntity<String> create(
            // @Valid invokes the validation and if it fails,
            // MethodArgumentNotValidException is thrown. By default Spring
            // translates MethodArgumentNotValidException into bad request
            // (HTTP 400)
            @Valid @RequestBody UserDto userDto) {

        // Case when the email has already been existed in the database should
        // be handled manually to return bad request (HTTP 400), not to throw
        // any exception and not to use an ExceptionHandler
        if (userRepo.existsByEmail(userDto.getEmail())) {
            String message = "Email \"" + userDto.getEmail() + "\" exists";
            return ResponseEntity.badRequest().body(message);
        }

        UserEntity user = UserAdapters.entityFromDto(userDto);
        UserEntity savedUser = userRepo.save(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/user")
                .path("/{id}")
                .buildAndExpand(savedUser.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> update(
            @PathVariable Long id,
            @Valid @RequestBody UserDto userDto) {

        Optional<UserEntity> userOpt = userRepo.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        UserEntity user = userOpt.get();

        // Case when the email is not equal to the current email of the user
        // and has already been existed in the database should be handled
        // manually to return bad request (HTTP 400), not to throw any exception
        // and not to use an ExceptionHandler
        String email = userDto.getEmail();
        if (!email.equals(user.getEmail()) && userRepo.existsByEmail(email)) {
            String message = "Email \"" + email + "\" exists";
            return ResponseEntity.badRequest().body(message);
        }

        UserAdapters.setEntityByDto(user, userDto);
        userRepo.save(user);

        return ResponseEntity.ok("Updated");
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<String> delete(@PathVariable Long id) {

        ResponseEntity<String> response = userRepo.existsById(id)
                ? ResponseEntity.ok("Deleted")
                : ResponseEntity.notFound().build();

        userRepo.deleteById(id);

        return response;
    }
}
