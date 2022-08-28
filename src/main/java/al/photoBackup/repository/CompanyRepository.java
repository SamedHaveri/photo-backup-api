package al.photoBackup.repository;

import al.photoBackup.model.entity.CompanyEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyRepository extends CrudRepository<CompanyEntity, Integer> {

	CompanyEntity findByName(String name);

	@Query("select c from CompanyEntity as c")
	List<CompanyEntity> getAll();

	@Query("select c from CompanyEntity as c where c.paymentDeadline < current_date")
	List<CompanyEntity> getExpired();

	@Query("select c from CompanyEntity as c where c.paymentDeadline > current_date")
	List<CompanyEntity> getActive();

	void deleteById(Integer id);

	@Query("SELECT c.id from CompanyEntity as c where c.name = :name")
	Integer getId(@Param("name") String name);

}
