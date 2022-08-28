package al.photoBackup.repository;

import al.photoBackup.model.constant.Status;
import al.photoBackup.model.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends PagingAndSortingRepository<UserEntity, Integer> {

	Page<UserEntity> getByUsernameContainingAndCompanyEntity_idAndStatus(String name, Integer companyId, Integer status, Pageable pageable);

	Page<UserEntity> getByCompanyEntity_idAndStatus(Integer companyId, Integer status, Pageable pageable);

	UserEntity getByIdAndCompanyEntity_Id(Integer id, Integer companyId);

	@Query("SELECT u.id from UserEntity as u where u.username = :username and u.id <> :userId")
	Integer getIdOfUserWithIdNotAndUsername(Integer userId, String username);

	Optional<UserEntity> findByUsername(String username);

	Optional<UserEntity> findByUsernameAndStatusIs(String username, int status);

	Optional<List<UserEntity>> findByUsernameContainsAndStatusIs(String username, int status);

	UserEntity getById(Integer id);

	List<UserEntity> findByStatusIs(int status);

	@Override
	void delete(UserEntity userEntity);

	void deleteById(Integer id);

	@Query("SELECT u.id from UserEntity as u where u.username = :username")
	Integer getId(@Param("username") String username);

	@Query("SELECT u from UserEntity as u where u.status = :userStatus")
	List<UserEntity> getByStatus(@Param("userStatus") Status status);

	@Query("select u from UserEntity as u where u.companyEntity.id = :companyId")
	List<UserEntity> getByCompanyId(Integer companyId);

}
