package al.photoBackup.config;

import al.photoBackup.exception.company.CompanyNameExistsException;
import al.photoBackup.exception.user.UserNameNotFoundException;
import al.photoBackup.model.constant.Role;
import al.photoBackup.model.constant.Status;
import al.photoBackup.model.dto.company.CompanyInsertDTO;
import al.photoBackup.model.entity.CompanyEntity;
import al.photoBackup.model.entity.UserEntity;
import al.photoBackup.service.CompanyService;
import al.photoBackup.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

	private final UserService userService;
	private final CompanyService companyService;
	private final PasswordEncoder passwordEncoder;

	private boolean alreadySetup = true;

//	@Value("#{'${al.equity.fiskalization.b2b.currencies}'.split(',')}")
//	private List<String> currencies;
//
//	@Value("#{'${al.equity.fiskalization.b2b.currencies.signs}'.split(',')}")
//	private List<String> currencySigns;
//
//	@Value("#{'${al.equity.fiskalization.b2b.receipt.types}'.split(',')}")
//	private List<String> receiptTypes;

	@Autowired
	public SetupDataLoader(UserService userService, CompanyService companyService,
						   PasswordEncoder passwordEncoder) {
		this.userService = userService;
		this.companyService = companyService;
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

//	private void createReceiptTypes() {
//		if (receiptTypeService.total() == 0)
//			receiptTypes.forEach(t -> receiptTypeService.save(new ReceiptTypeEntity(t)));
//	}
//
//	private void createCurrencies() {
//		if (currencyService.total() == 0) {
//			for (int i = 0; i < currencies.size(); i++) {
//				currencyService.save(new CurrencyEntity(currencies.get(i), currencySigns.get(i)));
//			}
//		}
//	}

	@Transactional
	void createUserIfNotFound() {
		try {
			userService.getActiveByUsername("username");
		} catch (UserNameNotFoundException e) {
			final var company = createTestCompany();
			var user = new UserEntity();
			user.setUsername("username");
			user.setPassword(passwordEncoder.encode("password"));
			user.setRole(Role.ROLE_SYSADMIN.name());
			user.setStatus(Status.ACTIVE.getStatus());
			user.setCompanyEntity(company);
			try {
				System.out.println("saving user: " + user);
				userService.save(user);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private CompanyEntity createTestCompany() {
		try {
			return companyService.save(new CompanyInsertDTO("test", 20, LocalDate.now().plusMonths(12)));
		} catch (CompanyNameExistsException e) {
			e.printStackTrace();
		}
		return companyService.getByName("test");
	}

}
