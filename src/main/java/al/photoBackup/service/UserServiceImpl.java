package al.photoBackup.service;

import al.photoBackup.exception.auth.WrongPasswordException;
import al.photoBackup.exception.user.UserIdNotFoundException;
import al.photoBackup.exception.user.UserNameExistsException;
import al.photoBackup.exception.user.UserNameNotFoundException;
import al.photoBackup.exception.user.UserRoleNameNotFoundException;
import al.photoBackup.model.constant.Role;
import al.photoBackup.model.constant.Status;
import al.photoBackup.model.dto.user.*;
import al.photoBackup.model.entity.CompanyEntity;
import al.photoBackup.model.entity.UserEntity;
import al.photoBackup.repository.UserRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;

	public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
						   @Lazy AuthenticationManager authenticationManager) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
	}

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		final var user = userRepository.findByUsername(username).orElse(null);
		if (user == null)
			throw new UsernameNotFoundException("Përdoruesi me emrin e kërkuar nuk u gjet!");
		return SecurityUserDetails.build(user);
	}

	@Override
	public UserEntity getById(Integer id, Integer companyId) {
		return userRepository.getByIdAndCompanyEntity_Id(id, companyId);
	}

	@Override
	public UserEntity getActiveByUsername(String username) throws UserNameNotFoundException {
		return userRepository
				.findByUsernameAndStatusIs(username, Status.ACTIVE.getStatus())
				.orElseThrow(UserNameNotFoundException::new);
	}

	@Override
	public List<UserEntity> getAllActive() {
		return userRepository.findByStatusIs(Status.ACTIVE.getStatus());
	}

	@Override
	public UserPageResponseDTO searchInCompany(String name, Integer pageNumber, Integer pageSize, String sortColumn,
                                               String sortOrder, Integer companyId) {
		Page<UserEntity> users;
		if (name != null && !name.isEmpty()) //search by name if defined
			users = userRepository.getByUsernameContainingAndCompanyEntity_idAndStatus(name, companyId, Status.ACTIVE.getStatus(),
					PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.fromString(sortOrder), sortColumn)));
		else
			users = userRepository.getByCompanyEntity_idAndStatus(companyId, Status.ACTIVE.getStatus(),
					PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.fromString(sortOrder), sortColumn)));
		final var res = new UserPageResponseDTO();
		res.setTotalElements(users.getTotalElements());
		res.setTotalPages(users.getTotalPages());
		res.setUsers(users
				.stream()
				.map(UserResponseDTO::new)
				.collect(Collectors.toList()));
		return res;
	}

	@Override
	public void disableUser(Integer id, Integer companyId) throws UserNameNotFoundException {
		final var userFound = userRepository.getByIdAndCompanyEntity_Id(id, companyId);
		if (userFound == null)
			throw new UserNameNotFoundException();
		userFound.setStatus(Status.DISABLED.getStatus());
		userRepository.save(userFound);
	}

	@Override
	public UserEntity save(UserInsertDTO dto, Integer companyId)
			throws UserNameExistsException, UserRoleNameNotFoundException {
		final var idFromUsername = userRepository.getId(dto.getName());
		if (idFromUsername != null)
			throw new UserNameExistsException();
		final var userRoleFound = getNonSystemRole(dto.getRole());
		final var company = new CompanyEntity(companyId);
		var newUserEntity = new UserEntity();
		newUserEntity.setCompanyEntity(company);
		newUserEntity.setUsername(dto.getName().trim());
		newUserEntity.setPassword(passwordEncoder.encode(dto.getPassword()));
		newUserEntity.setRole(userRoleFound.name());
		newUserEntity.setStatus(Status.ACTIVE.getStatus());
		return userRepository.save(newUserEntity);
	}

	@Override
	public UserEntity save(UserEntity userEntity) throws UserNameExistsException {
		final var idFoundByUsername = userRepository.getId(userEntity.getUsername());
		if (idFoundByUsername != null)
			throw new UserNameExistsException();
		return userRepository.save(userEntity);
	}

	@Override
	public UserEntity update(UserUpdateDTO dto, Integer companyId)
			throws UserIdNotFoundException, UserRoleNameNotFoundException, UserNameExistsException {
		final var existingUsernameFound = userRepository
				.getIdOfUserWithIdNotAndUsername(dto.getId(), dto.getName());
		if (existingUsernameFound != null)
			throw new UserNameExistsException();
		final var userFound = userRepository.getByIdAndCompanyEntity_Id(dto.getId(), companyId);
		if (userFound == null)
			throw new UserIdNotFoundException();
		final var roleFound = getNonSystemRole(dto.getRole());
		if (roleFound == null)
			throw new UserRoleNameNotFoundException();

		userFound.setRole(roleFound.name());
		userFound.setPassword(passwordEncoder.encode(dto.getPassword()));
		userFound.setUsername(dto.getName().trim());
		return userRepository.save(userFound);
	}

	/**
	 * try to authenticate with old password, if successful update password
	 *
	 * @throws WrongPasswordException on authentication failure
	 */
	@Override
	public UserEntity updatePassword(String currentLoggedUsername, UserPasswordDTO dto)
			throws WrongPasswordException {
		final var authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(currentLoggedUsername, dto.getCurrentPassword()));
		if (!authentication.isAuthenticated())
			throw new WrongPasswordException();
		//get and update password
		final var currentUser = userRepository.findByUsername(currentLoggedUsername).get();
		currentUser.setPassword(passwordEncoder.encode(dto.getNewPassword()));
		return userRepository.save(currentUser);
	}

	@Override
	public Integer getId(String username) {
		return userRepository.getId(username);
	}

	@Override
	public List<UserEntity> getByCompanyId(Integer companyId) {
		return userRepository.getByCompanyId(companyId);
	}

	@Override
	public List<Role> getAllRoles() {
		return Stream.of(Role.values()).collect(Collectors.toList());
	}

	@Override
	public List<String> getNonSystemRoles() {
		return Stream.of(Role.values())
				.map(Enum::name)
				.filter(r -> !r.toLowerCase().contains("sys"))
				.collect(Collectors.toList());
	}

	@Override
	public Role getRole(String role) throws UserRoleNameNotFoundException {
		if (!role.startsWith("ROLE_"))
			role = "ROLE_" + role;
		final var roleFound = Role.getRoleFromName(role.toUpperCase());
		if (roleFound == null)
			throw new UserRoleNameNotFoundException();
		else return roleFound;
	}

	@Override
	public Role getNonSystemRole(String role) throws UserRoleNameNotFoundException {
		if (role.toLowerCase().contains("sys"))
			throw new UserRoleNameNotFoundException();
		if (!role.startsWith("ROLE_"))
			role = "ROLE_" + role;
		final var roleFound = Role.getRoleFromName(role.toUpperCase());
		if (roleFound == null)
			throw new UserRoleNameNotFoundException();
		else return roleFound;
	}

}
