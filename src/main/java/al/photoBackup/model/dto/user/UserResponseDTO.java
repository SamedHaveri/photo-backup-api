package al.photoBackup.model.dto.user;

import al.photoBackup.model.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {
	private Integer id;
	private String username;

	public UserResponseDTO(UserEntity u) {
		this.id = u.getId();
		this.username = u.getUsername();
	}
}
