package druyaned.aston.intensive.userservice.serve;

import druyaned.aston.intensive.userservice.model.UserConversion;
import druyaned.aston.intensive.userservice.model.UserDto;
import druyaned.aston.intensive.userservice.model.UserEntity;
import druyaned.aston.intensive.userservice.repo.UserRepository;
import druyaned.aston.intensive.userservice.serve.UserService.Result.Type;
import static druyaned.aston.intensive.userservice.serve.UserService.Result.Type.CREATED;
import static druyaned.aston.intensive.userservice.serve.UserService.Result.Type.DELETED;
import static druyaned.aston.intensive.userservice.serve.UserService.Result.Type.EMAIL_DUPLICATION;
import static druyaned.aston.intensive.userservice.serve.UserService.Result.Type.FOUND;
import static druyaned.aston.intensive.userservice.serve.UserService.Result.Type.NOT_FOUND;
import static druyaned.aston.intensive.userservice.serve.UserService.Result.Type.UPDATED;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Mediator between repository and controller. Task requires several CRUD operations: {@link #get},
 * {@link #create}, {@link #update}, {@link #delete}. Each CRUD method return {@link Result}.
 *
 * @author druyaned
 */
@Service
public class UserService {

    /**
     * The returning of all CRUD methods provided by {@link UserService}; serves to define some
     * special behavior and to avoid exceptions in some quite expected cases.
     */
    public static record Result(Type type, String message, UserDto content) {

        /**
         * Describes all possible returning types of CRUD methods.
         *
         * @see UserService
         */
        public static enum Type {
            NOT_FOUND, EMAIL_DUPLICATION, FOUND, CREATED, UPDATED, DELETED
        }

        public static Result notFound(Long id) {
            return new Result(NOT_FOUND, "User is NOT FOUND by ID=" + id, null);
        }

        public static Result emailDuplication(String email) {
            return new Result(EMAIL_DUPLICATION, "Email \"" + email + "\" exists", null);
        }

        public static Result found(UserDto userDto) {
            return new Result(FOUND, "User is FOUND by ID=" + userDto.getId(), userDto);
        }

        public static Result created(UserDto userDto) {
            return new Result(CREATED, "User is CREATED by ID=" + userDto.getId(), userDto);
        }

        public static Result updated(UserDto userDto) {
            return new Result(UPDATED, "User is UPDATED by ID=" + userDto.getId(), userDto);
        }

        public static Result deleted(UserDto userDto) {
            return new Result(DELETED, "User is DELETED by ID=" + userDto.getId(), userDto);
        }
    }

    private final UserRepository userRepo;

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * Returns all users according to the given pageable.
     *
     * @param pageable to define the page of the desired result
     * @return all users according to the given pageable
     */
    public List<UserDto> getAll(Pageable pageable) {
        Stream<UserEntity> userStream = userRepo.findAll(pageable).stream();

        return userStream.collect(ArrayList::new, (list, user)
                -> list.add(UserConversion.dtoFromEntity(user)), ArrayList::addAll);
    }

    /**
     * Returns found-result with the user or not-found-result.
     *
     * @param id to find user
     * @return found-result with the user or not-found-result
     */
    public Result get(Long id) {
        Optional<UserEntity> userOpt = userRepo.findById(id);

        return userOpt.isPresent()
                ? Result.found(UserConversion.dtoFromEntity(userOpt.get()))
                : Result.notFound(id);
    }

    /**
     * Creates user according to the given DTO and returns created-result with the user or
     * email-duplication-result if email of the given DTO has already been stored.
     *
     * @param userDto to create a new user
     *
     * @return created-result with the user or email-duplication-result if email of the given DTO
     * has already been stored
     */
    public Result create(UserDto userDto) {
        if (userRepo.existsByEmail(userDto.getEmail())) {
            return Result.emailDuplication(userDto.getEmail());
        }

        UserEntity savedUser = userRepo.save(UserConversion.entityFromDto(userDto));

        return Result.created(UserConversion.dtoFromEntity(savedUser));
    }

    /**
     * Updates an existing user by the ID and the given DTO, returns updated-result in case of
     * update, not-found-result in case of ID absence, email-duplication-result if the email can't
     * be stored (is not equal to the previous email and has already been presented).
     *
     * @param id to found the user that should be updated
     * @param userDto to make an update according to this DTO
     *
     * @return updated-result in case of update, not-found-result in case of ID absence,
     * email-duplication-result if the email can't be stored (is not equal to the previous email and
     * has already been presented)
     */
    public Result update(Long id, UserDto userDto) {
        Optional<UserEntity> userOpt = userRepo.findById(id);

        if (userOpt.isEmpty()) {
            return Result.notFound(id);
        }

        UserEntity user = userOpt.get();
        String givenEmail = userDto.getEmail();

        if (!givenEmail.equals(user.getEmail()) && userRepo.existsByEmail(givenEmail)) {
            return Result.emailDuplication(givenEmail);
        }

        UserConversion.setEntityByDto(user, userDto);
        userRepo.save(user);

        return Result.updated(UserConversion.dtoFromEntity(user));
    }

    /**
     * Deletes user by the given ID, returns deleted-result or not-found-result if user was not
     * found by the given ID.
     *
     * @param id to found the user that should be deleted
     *
     * @return deleted-result or not-found-result if user was not found by the given ID
     */
    public Result delete(Long id) {
        Optional<UserEntity> userOpt = userRepo.findById(id);

        if (userOpt.isEmpty()) {
            return Result.notFound(id);
        }

        UserDto userDto = UserConversion.dtoFromEntity(userOpt.get());

        userRepo.deleteById(id);

        return Result.deleted(userDto);
    }
}
