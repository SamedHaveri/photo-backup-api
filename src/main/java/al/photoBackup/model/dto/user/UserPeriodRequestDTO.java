package al.photoBackup.model.dto.user;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class UserPeriodRequestDTO {
	@NotNull(message = "Përcaktoni ID")
	@Min(value = 1, message = "ID nuk është e saktë")
	private Integer id;

	@NotNull(message = "Përcaktoni datën e fillimit")
	private LocalDate from;

	@NotNull(message = "Përcaktoni datën e përfundimit")
	private LocalDate to;
}
