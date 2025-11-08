package druyaned.aston.intensive.userservice.model;

import java.time.OffsetDateTime;

/**
 * Provides some utilities to make conversion between {@link
 * UserEntity} and {@link UserDto} properly.
 *
 * <p>
 * P.S. Usage of a basic Adapter Pattern leads to unlimited nesting that's why
 * it's naive to apply it here.
 *
 * @author druyaned
 */
public class UserAdapters {

    /**
     * Creates a new {@code UserEntity} and sets every field of the entity as in
     * the given DTO except {@code id} (sets null) and {@code createdAt} (sets
     * the current time).
     *
     * @param userDto to set a new entity
     *
     * @return a new entity with set field as in the DTO except {@code id} (sets
     * null) and {@code createdAt} (sets the current time)
     */
    public static UserEntity entityFromDto(UserDto userDto) {
        UserEntity user = new UserEntity();

        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setBirthdate(userDto.getBirthdate());

        user.setCreatedAt(OffsetDateTime.now());

        return user;
    }

    /**
     * Creates a new DTO and sets every field of the DTO as in the entity.
     *
     * @param user to set fields of a new DTO
     *
     * @return a new DTO and sets every field of the DTO as in the entity
     */
    public static UserDto dtoFromEntity(UserEntity user) {
        UserDto userDto = new UserDto();

        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        userDto.setBirthdate(user.getBirthdate());
        userDto.setCreatedAt(user.getCreatedAt());

        return userDto;
    }

    /**
     * Sets every field of the entity as in the DTO except {@code id} and
     * {@code createdAt}.
     *
     * @param user to set its field except {@code id} and {@code createdAt}
     * @param userDto to set the entity
     */
    public static void setEntityByDto(UserEntity user, UserDto userDto) {
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setBirthdate(userDto.getBirthdate());
    }
}
