package al.photoBackup.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPasswordDTO {
	@NotBlank(message = "Fjalëkalimi nuk mund të jetë bosh")
	@Size(min = 6, message = "Fjalëkalimi duhet të ketë të paktën 6 karaktere")
	private String currentPassword;

	@NotBlank(message = "Fjalëkalimi nuk mund të jetë bosh")
	@Size(min = 6, message = "Fjalëkalimi duhet të ketë të paktën 6 karaktere")
	private String newPassword;
}
