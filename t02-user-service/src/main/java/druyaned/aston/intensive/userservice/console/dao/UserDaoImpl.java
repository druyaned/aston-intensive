package druyaned.aston.intensive.userservice.dao;

import druyaned.aston.intensive.userservice.model.UserEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;
import java.util.function.Supplier;
import org.hibernate.SessionFactory;

/**
 * Implementation of User Data Access Object.
 *
 * <p>
 * <b>Task requirement</b>: "Configure transactionality for database
 * operations". Every main operation here uses one from the custom
 * {@code inTransaction} methods. Of course, I could use Hibernate's
 * {@link SessionFactory#inTransaction inTransaction} method, but I wanted to
 * implement it myself, because it's important to know and practice with.
 *
 * @author druyaned
 */
public class UserDaoImpl implements UserDao {

    private final EntityManager entityManager;

    public UserDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void save(UserEntity user) {
        inTransaction(() -> entityManager.persist(user));
    }

    @Override
    public List<UserEntity> findAll() {
        return inTransaction(() -> entityManager
                .createQuery("from UserEntity", UserEntity.class)
                .getResultList());
    }

    @Override
    public UserEntity find(Long id) {
        return inTransaction(() -> entityManager.find(UserEntity.class, id));
    }

    @Override
    public void update(UserEntity user) {
        inTransaction(() -> entityManager.merge(user));
    }

    @Override
    public void delete(UserEntity user) {
        inTransaction(() -> entityManager.remove(user));
    }

    @Override
    public boolean emailExists(String email) {
        String hql = "select 1 from UserEntity where email = :email";
        return inTransaction(() -> entityManager
                .createQuery(hql, Integer.class)
                .setParameter("email", email)
                .getResultList()
                .isEmpty()
                == false);
    }

    private void inTransaction(Runnable runnable) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            runnable.run();
            transaction.commit();
        } catch (Exception exc) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw exc;
        }
    }

    private <T> T inTransaction(Supplier<T> supplier) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            T result = supplier.get();
            transaction.commit();
            return result;
        } catch (Exception exc) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw exc;
        }
    }
}
