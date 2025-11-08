package druyaned.aston.intensive.userservice.dao;

import druyaned.aston.intensive.userservice.App;
import druyaned.aston.intensive.userservice.model.UserEntity;
import java.util.List;

/**
 * Data Access Object for the {@link UserEntity}.
 *
 * <p>
 * <b>Task#02 requirements</b>:
 * <ul>
 * <li>Implement CRUD operations for the UserEntity entity (save, read, update,
 * delete).</li>
 * <li>Use the DAO pattern to separate the logic of working with the
 * database.</li>
 * </ul>
 *
 * <p>
 * <b>Hibernate documentation page says, considering DAOs and Repositories</b>:
 * "Our considered view is that they’re mostly just bloat. The JPA EntityManager
 * is a 'repository', and it’s a standard repository with a well-defined
 * specification written by people who spend all day thinking about persistence.
 * If these repository frameworks offered anything actually useful — and not
 * obviously foot-shooty — over and above what EntityManager provides, we would
 * have already added it to EntityManager decades ago".
 *
 * <p>
 * To my mind the DAO pattern is really unnecessary here, EntityManager does all
 * the work. But it's useful to practice, to be more familiar with such a
 * pattern.
 *
 * @author druyaned
 * @see App
 */
public interface UserDao {

    /**
     * Persists the given user if it's valid into the database.
     *
     * @implNote this method must be transactional
     *
     * @param user to be persisted
     */
    void save(UserEntity user);

    /**
     * Gets all users stored in the database.
     *
     * @implNote this method must be transactional
     *
     * @return all users stored in the database or an empty list if there are no
     * users
     */
    List<UserEntity> findAll();

    /**
     * Gets a users by the given id from the database.
     *
     * @implNote this method must be transactional
     *
     * @param id to find user
     *
     * @return found user or null if it was not found
     */
    UserEntity find(Long id);

    /**
     * Updates all fields of the given the user in the database. In case of
     * inability to update does nothing.
     *
     * @implNote this method must be transactional
     *
     * @param user to update all its fields in the database
     */
    void update(UserEntity user);

    /**
     * Removes the user from the database. If the user is not in the database,
     * this method does nothing.
     *
     * @implNote this method must be transactional
     *
     * @param user to be removed
     */
    void delete(UserEntity user);

    /**
     * Checks if the given email is in the database.
     *
     * @implNote this method must be transactional
     *
     * @param email to check the presence
     *
     * @return true if the email is in the database, otherwise false
     */
    boolean emailExists(String email);
}
