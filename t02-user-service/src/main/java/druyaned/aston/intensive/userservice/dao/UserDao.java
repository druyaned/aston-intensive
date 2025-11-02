package druyaned.aston.intensive.userservice.dao;

import druyaned.aston.intensive.userservice.model.User;
import java.util.List;

/**
 * Data Access Object for the {@link User} to meet the task requirements.
 * 
 * <p>
 * <b>Task requirements</b>:
 * <ul>
 *   <li>Implement CRUD operations for the User entity (save, read, update,
     delete).</li>
 *   <li>Use the DAO pattern to separate the logic of working with the
 *     database.</li>
 * </ul>
 * 
 * <p>
 * <b>Hibernate documentation page says, considering DAOs and Repositories</b>:
 * "Our considered view is that they’re mostly just bloat. The JPA
 * EntityManager is a 'repository', and it’s a standard repository with a
 * well-defined specification written by people who spend all day thinking
 * about persistence. If these repository frameworks offered anything actually
 * useful — and not obviously foot-shooty — over and above what EntityManager
 * provides, we would have already added it to EntityManager decades ago".
 * 
 * <p>
 * To my mind the DAO pattern is really unnecessary here, EntityManager does
 * all the work. But it's useful to practice, to be more familiar with such
 * a pattern.
 * 
 * @author druyaned
 * @see druyaned.aston.intensive.userservice.App
 */
public interface UserDao {
    
    void save(User user);
    
    List<User> findAll();
    
    User find(Long id);
    
    void update(User user);
    
    void delete(User user);
    
    boolean emailExists(String email);
}
