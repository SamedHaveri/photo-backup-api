package al.photoBackup.repository;

import al.photoBackup.model.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {
    @Query("select f from FileEntity as f where f.id = :id and f.userEntity.id = :userId")
    FileEntity getByIdAndUsername(@PathVariable("id") Long id, @PathVariable("userId") Integer userId);
    List<FileEntity> getByUserEntity_Id(Integer userId);
}
