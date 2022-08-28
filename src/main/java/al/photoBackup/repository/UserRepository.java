package al.photoBackup.repository;

import al.photoBackup.model.entity.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends PagingAndSortingRepository<UserEntity, Integer> {

	@Query("SELECT case when count(u) > 0 then true else false end from UserEntity as u where u.username = :username and u.id <> :userId")
	Boolean existsByIdAndNotUsername(Integer userId, String username);

	Optional<UserEntity> findByUsername(String username);

	Optional<UserEntity> findByUsernameAndStatusIs(String username, int status);

	UserEntity getById(Integer id);

	@Override
	void delete(UserEntity userEntity);

	void deleteById(Integer id);

	@Query("SELECT u.id from UserEntity as u where u.username = :username")
	Integer getId(@Param("username") String username);

}
