package al.photoBackup.model.dto.user;

import lombok.Data;

import java.util.List;

@Data
public class UserPageResponseDTO {
	private Long totalElements;
	private Integer totalPages;
	private List<UserResponseDTO> users;
}
