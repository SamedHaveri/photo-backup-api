package al.photoBackup.model.dto.company;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CompanyUpdateDTO {
	@NotNull(message = "Përcaktoni ID")
	@Min(value = 1, message = "ID nuk është e saktë")
	private Integer id;

	@NotBlank(message = "Emërtimi nuk mund të jetë bosh")
	@Size(min = 2, message = "Emërtimi duhet të ketë të paktën 2 karaktere")
	private String name;

	@Min(value = 1, message = "Nevojitet të paktën një përdorues")
	private Integer maxUsers;

	@NotNull(message = "Përcaktoni afatin e pagesës")
	@JsonFormat(pattern="MM/dd/yyy")
	private LocalDate paymentDeadline;
}
