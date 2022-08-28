package al.photoBackup.model.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "company")
public class CompanyEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(unique = true, name = "name", nullable = false, length = 80)
	private String name;

	@Column(name = "max_users")
	private Integer maxUsers;

	@Column(name = "payment_deadline")
	private LocalDate paymentDeadline;

	/**
	 * used to set as foreign key
	 */
	public CompanyEntity(Integer id) {
		this.id = id;
	}

}
