package al.photoBackup.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
public class UserEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "password", nullable = false)
	private String password;

	@Column(unique = true, name = "username", nullable = false)
	private String username;

	@Column(name = "status")
	private int status;

	@NotBlank(message = "Përcaktoni rolin e përdoruesit")
	private String role;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
	@JoinColumn(name = "company_id")
	@ToString.Exclude
	private CompanyEntity companyEntity;

}
