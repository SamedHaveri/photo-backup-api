package al.photoBackup.config;

import al.photoBackup.exception.user.UserNameNotFoundException;
import al.photoBackup.model.constant.Status;
import al.photoBackup.model.entity.UserEntity;
import al.photoBackup.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

	private final UserService userService;
	private final PasswordEncoder passwordEncoder;

	private boolean alreadySetup = false;

//	@Value("#{'${al.equity.fiskalization.b2b.currencies}'.split(',')}")
//	private List<String> currencies;
//
//	@Value("#{'${al.equity.fiskalization.b2b.currencies.signs}'.split(',')}")
//	private List<String> currencySigns;
//
//	@Value("#{'${al.equity.fiskalization.b2b.receipt.types}'.split(',')}")
//	private List<String> receiptTypes;

	@Autowired
	public SetupDataLoader(UserService userService,
						   PasswordEncoder passwordEncoder) {
		this.userService = userService;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	@Transactional
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (alreadySetup)
			return;
		System.out.println("initial setup is running...");
		createUserIfNotFound();
		alreadySetup = true;
	}

	@Transactional
	void createUserIfNotFound() {
		try {
			userService.getByUsername("username");
		} catch (UserNameNotFoundException e) {
			var user = new UserEntity();
			user.setUsername("username");
			user.setPassword(passwordEncoder.encode("password"));
			user.setStatus(Status.ACTIVE.getStatus());
			user.setUniqueFolder(user.getUsername() +"-"+LocalDateTime.now());
			try {
				System.out.println("saving user: " + user);
				userService.save(user);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

}
