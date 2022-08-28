package al.photoBackup.model.dto.user;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class UserInsertDTO {
	@NotBlank(message = "Emri nuk mund të jetë bosh")
	@Size(min = 3, message = "Emri duhet të ketë të paktën 3 karaktere")
	private String name;

	@NotBlank(message = "Fjalëkalimi nuk mund të jetë bosh")
	@Size(min = 6, message = "Fjalëkalimi duhet të ketë të paktën 6 karaktere")
	private String password;

	@NotBlank(message = "Roli nuk mund të jetë bosh")
	private String role;
}