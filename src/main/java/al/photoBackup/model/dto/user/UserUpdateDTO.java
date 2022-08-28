package al.photoBackup.model.dto.user;

import lombok.*;

import javax.validation.constraints.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDTO {
	@NotNull(message = "Përcaktoni ID")
	@Min(value = 1, message = "ID nuk është e saktë")
	private Integer id;

	@NotBlank(message = "Emri nuk mund të jetë bosh")
	@Size(min = 3, message = "Emri duhet të ketë të paktën 3 karaktere")
	private String name;

	@NotBlank(message = "Fjalëkalimi nuk mund të jetë bosh")
	@Size(min = 6, message = "Fjalëkalimi duhet të ketë të paktën 6 karaktere")
	private String password;

	@NotEmpty(message = "Përcaktoni rolin e përdoruesit")
	private String role;
}
