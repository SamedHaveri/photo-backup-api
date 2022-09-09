package al.photoBackup.model.dto.file;

import al.photoBackup.model.entity.FileEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileResponseDTO {
    private Long id;
    private String name;
    private Long size;
    private Integer userId;

    public FileResponseDTO(FileEntity entity){
        this.id = entity.getId();
        this.name = entity.getName();
        this.size = entity.getSize();
        this.userId = entity.getUserEntity().getId();
    }
}
