package al.photoBackup.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthenticationRequest {
	@NotEmpty(message = "Username nuk mund të jetë bosh")
	private String username;

	@NotEmpty(message = "Password nuk mund të jetë bosh")
	private String password;
}
