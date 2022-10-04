package al.photoBackup.model.dto.image;

import al.photoBackup.model.entity.MediaEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MediaResponseDTO {
    private Long id;
    private String name;
    private Long size;

    public MediaResponseDTO(MediaEntity entity){
        this.id = entity.getId();
        this.name = entity.getName();
        this.size = entity.getSize();
    }
}
