package al.photoBackup.model.dto.image;

import al.photoBackup.model.entity.ImageEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageResponseDTO {
    private Long id;
    private String name;
    private Long size;

    public ImageResponseDTO(ImageEntity entity){
        this.id = entity.getId();
        this.name = entity.getName();
        this.size = entity.getSize();
    }
}
