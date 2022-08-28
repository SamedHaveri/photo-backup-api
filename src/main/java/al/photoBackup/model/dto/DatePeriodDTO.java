package al.photoBackup.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class DatePeriodDTO {
	@NotNull(message = "Përcaktoni fillimin")
	private LocalDate from;

	@NotNull(message = "Përcaktoni fundin")
	private LocalDate to;

	public DatePeriodDTO(LocalDate from, LocalDate to) {
		this.from = from;
		this.to = to;
	}

	public DatePeriodDTO() {
	}
}
