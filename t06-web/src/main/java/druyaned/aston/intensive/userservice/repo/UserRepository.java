package druyaned.aston.intensive.userservice.repo;

import druyaned.aston.intensive.userservice.model.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository // for exception translation mostly
public interface UserRepository extends CrudRepository<UserEntity, Long>,
        PagingAndSortingRepository<UserEntity, Long> {

    boolean existsByEmail(String email);
}
