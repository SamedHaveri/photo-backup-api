package al.photoBackup.service;

import al.photoBackup.exception.auth.WrongPasswordException;
import al.photoBackup.exception.user.UserIdNotFoundException;
import al.photoBackup.exception.user.UserNameExistsException;
import al.photoBackup.exception.user.UserNameNotFoundException;
import al.photoBackup.model.constant.Status;
import al.photoBackup.model.dto.user.SecurityUserDetails;
import al.photoBackup.model.dto.user.UserPasswordDTO;
import al.photoBackup.model.dto.user.UserUpdateDTO;
import al.photoBackup.model.entity.UserEntity;
import al.photoBackup.repository.UserRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	public UserEntity getByUsername(String username) throws UserNameNotFoundException {
		return userRepository
				.findByUsername(username)
				.orElseThrow(UserNameNotFoundException::new);
	}

	@Override
	@Transactional
	public void disableUser(Integer id) throws UserNameNotFoundException {
		var userFound = userRepository.getById(id);
		if (userFound == null)
			throw new UserNameNotFoundException();
		userFound.setStatus(Status.DISABLED.getStatus());
		userRepository.save(userFound);
	}

	@Override
	public UserEntity save(UserEntity userEntity) throws UserNameExistsException {
		final var idFoundByUsername = userRepository.getId(userEntity.getUsername());
		if (idFoundByUsername != null)
			throw new UserNameExistsException();
		return userRepository.save(userEntity);
	}

	@Override
	@Transactional
	public UserEntity update(UserUpdateDTO dto, Integer userId)
			throws UserIdNotFoundException, UserNameExistsException {
		if (userRepository.existsByIdAndNotUsername(userId, dto.getName()))
			throw new UserNameExistsException();
		final var userFound = userRepository.getById(userId);
		if (userFound == null)
			throw new UserIdNotFoundException();
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
			throws WrongPasswordException, UserNameNotFoundException {
		final var authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(currentLoggedUsername, dto.getCurrentPassword()));
		if (!authentication.isAuthenticated())
			throw new WrongPasswordException();
		//get and update password
		final var currentUser = userRepository.findByUsername(currentLoggedUsername)
				.orElseThrow(UserNameNotFoundException::new);
		currentUser.setPassword(passwordEncoder.encode(dto.getNewPassword()));
		return userRepository.save(currentUser);
	}

}
