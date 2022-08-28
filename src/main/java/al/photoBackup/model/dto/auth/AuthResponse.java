package al.photoBackup.model.dto.auth;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private Integer id;
    private String username;
    private String role;
    @JsonFormat(pattern="MM/dd/yyyy HH:mm:ss")
    private LocalDateTime tokenExpiration;
    private Integer companyId;
}
