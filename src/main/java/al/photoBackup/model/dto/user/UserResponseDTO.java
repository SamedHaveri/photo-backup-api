package al.photoBackup.model.dto.user;

import al.photoBackup.model.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {
	private Integer id, companyId;
	private String username, role;

	public UserResponseDTO(UserEntity u) {
		this.id = u.getId();
		this.username = u.getUsername();
		this.role = u.getRole();
		this.companyId = u.getCompanyEntity().getId();
	}
}
