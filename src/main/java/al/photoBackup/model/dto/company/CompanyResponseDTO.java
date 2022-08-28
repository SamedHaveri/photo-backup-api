package al.photoBackup.model.dto.company;

import al.photoBackup.model.entity.CompanyEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CompanyResponseDTO {

	private Integer id;
	private String name;
	private Integer maxUsers;
	@JsonFormat(pattern="MM/dd/yyy")
	private LocalDate paymentDeadline;

	public CompanyResponseDTO(CompanyEntity companyEntity) {
		this.id = companyEntity.getId();
		this.name = companyEntity.getName();
		this.paymentDeadline = companyEntity.getPaymentDeadline();
		this.maxUsers = companyEntity.getMaxUsers();
	}

}
