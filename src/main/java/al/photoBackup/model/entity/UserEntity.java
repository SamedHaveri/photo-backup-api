package al.photoBackup.model.entity;

import lombok.*;

import javax.persistence.*;

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

	@Column(name = "status", nullable = false)
	private int status;

}
