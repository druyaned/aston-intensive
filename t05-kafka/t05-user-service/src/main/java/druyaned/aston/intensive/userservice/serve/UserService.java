package druyaned.aston.intensive.userservice.serve;

import druyaned.aston.intensive.userservice.model.UserConversion;
import druyaned.aston.intensive.userservice.model.UserDto;
import druyaned.aston.intensive.userservice.model.UserEntity;
import druyaned.aston.intensive.userservice.repo.UserRepository;
import druyaned.aston.intensive.userservice.serve.UserService.Result.Type;
import static druyaned.aston.intensive.userservice.serve.UserService.Result.Type.CREATED;
import static druyaned.aston.intensive.userservice.serve.UserService.Result.Type.DELETED;
import static druyaned.aston.intensive.userservice.serve.UserService.Result.Type.FOUND;
import static druyaned.aston.intensive.userservice.serve.UserService.Result.Type.NOT_CREATED;
import static druyaned.aston.intensive.userservice.serve.UserService.Result.Type.NOT_FOUND;
import static druyaned.aston.intensive.userservice.serve.UserService.Result.Type.NOT_UPDATED;
import static druyaned.aston.intensive.userservice.serve.UserService.Result.Type.UPDATED;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Mediator between repository and controller. Task requires several operations: {@link #getAll},
 * {@link #get}, {@link #create}, {@link #update}, {@link #delete}. Each method return
 * {@link Result}.
 *
 * @author druyaned
 */
@Service
public class UserService {

    /**
     * The returning of all methods provided by {@link UserService}; serves to define some special
     * behavior and to avoid exceptions in some quite expected cases.
     *
     * @param <T> type of body of the result, exempli gratia {@link UserDto}
     */
    public static record Result<T>(Type type, String message, T content) {

        /**
         * Describes all possible returning types of UserService's methods.
         *
         * @see UserService
         */
        public static enum Type {
            FOUND, NOT_FOUND,
            CREATED, NOT_CREATED,
            UPDATED, NOT_UPDATED,
            DELETED
        }

        public static <T> Result<T> notFound(Long id) {
            return new Result<>(NOT_FOUND, "User was found by ID=" + id, null);
        }
    }

    private final UserRepository userRepo;

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * Returns found-result with all users according to the given pageable.
     *
     * @param pageable to define the page of the desired result
     * @return found-result with all users according to the given pageable
     */
    public Result<List<UserDto>> getAll(Pageable pageable) {
        Stream<UserEntity> userStream = userRepo.findAll(pageable).stream();

        return new Result<>(FOUND, "Users were found", userStream.collect(ArrayList::new,
                (list, user) -> list.add(UserConversion.dtoFromEntity(user)), ArrayList::addAll));
    }

    /**
     * Returns found-result with the user or not-found-result.
     *
     * @param id to found the user
     * @return found-result with the user or not-found-result
     */
    public Result<UserDto> get(Long id) {
        Optional<UserEntity> userOpt = userRepo.findById(id);

        return userOpt.isPresent()
                ? new Result<>(FOUND, "User was found by ID=" + id,
                        UserConversion.dtoFromEntity(userOpt.get()))
                : Result.notFound(id);
    }

    /**
     * Creates user according to the given DTO and returns created-result with the user or
     * not-created-result if email of the given DTO has already been stored.
     *
     * @param userDto to create a new user
     *
     * @return created-result with the user or not-created-result if email of the given DTO has
     * already been stored
     */
    public Result<UserDto> create(UserDto userDto) {
        // Case when the email has already been existed in the database should be handled manually
        if (userRepo.existsByEmail(userDto.getEmail())) {
            return new Result<>(NOT_CREATED, "Not created: email \"" + userDto.getEmail()
                    + "\" exists", null);
        }

        UserEntity savedUser = userRepo.save(UserConversion.entityFromDto(userDto));

        return new Result<>(CREATED, "User was created by ID=" + savedUser.getId(),
                UserConversion.dtoFromEntity(savedUser));
    }

    /**
     * Updates an existing user by the ID and the given DTO, returns updated-result in case of
     * update, not-found-result in case of ID absence, not-updated result if the email can't be
     * stored (is not equal to the previous email and has already been presented).
     *
     * @param id to found the user that should be updated
     * @param userDto to make an update according to this DTO
     *
     * @return updated-result in case of update, not-found-result in case of ID absence, not-updated
     * result if the email can't be stored (is not equal to the previous email and has already been
     * presented)
     */
    public Result<UserDto> update(Long id, UserDto userDto) {
        Optional<UserEntity> userOpt = userRepo.findById(id);

        if (userOpt.isEmpty()) {
            return Result.notFound(id);
        }

        UserEntity user = userOpt.get();

        // Case when the email is not equal to the current email of the user and has already been
        // existed in the database should be handled manually
        String givenEmail = userDto.getEmail();
        if (!givenEmail.equals(user.getEmail()) && userRepo.existsByEmail(givenEmail)) {
            return new Result<>(NOT_UPDATED, "Not updated: email \"" + givenEmail + " exists",
                    null);
        }

        UserConversion.setEntityByDto(user, userDto);
        userRepo.save(user);

        return new Result<>(UPDATED, "User with ID=" + id + " was updated",
                UserConversion.dtoFromEntity(user));
    }

    /**
     * Deletes user by the given ID, returns deleted-result or not-found-result if a user was not
     * found by the given ID.
     *
     * @param id to found the user that should be deleted
     *
     * @return deleted-result or not-found-result if a user was not found by the given ID
     */
    public Result<UserDto> delete(Long id) {
        Optional<UserEntity> userOpt = userRepo.findById(id);

        if (userOpt.isEmpty()) {
            return Result.notFound(id);
        }

        UserDto userDto = UserConversion.dtoFromEntity(userOpt.get());

        userRepo.deleteById(id);

        return new Result<>(DELETED, "User with ID=" + id + " was deleted", userDto);
    }
}
