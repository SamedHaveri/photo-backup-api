package al.photoBackup.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "al.ibc.sao.ticket.backend.email")
public class EmailConfig {
	private String name;
	private String receiver;
	private String sender;
	private String password;
	private String host;
	private int smtpPort;
}
