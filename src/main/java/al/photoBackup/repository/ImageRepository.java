package al.photoBackup.repository;

import al.photoBackup.model.entity.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<ImageEntity, Long> {
    @Query("select i from ImageEntity as i where i.id = :id and i.userEntity.id = :userId")
    ImageEntity getByIdAndUsername(@PathVariable("id") Long id, @PathVariable("userId") Integer userId);
    List<ImageEntity> getByUserEntity_Id(Integer userId);
}
