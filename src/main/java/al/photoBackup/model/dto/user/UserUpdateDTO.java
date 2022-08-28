package al.photoBackup.model.dto.user;

import lombok.*;

import javax.validation.constraints.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDTO {
	@NotBlank(message = "Emri nuk mund të jetë bosh")
	@Size(min = 3, message = "Emri duhet të ketë të paktën 3 karaktere")
	private String name;
}
