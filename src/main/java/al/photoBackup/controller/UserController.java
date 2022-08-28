package al.photoBackup.controller;


import al.photoBackup.exception.auth.WrongPasswordException;
import al.photoBackup.exception.company.CompanyNotFoundException;
import al.photoBackup.exception.user.UserIdNotFoundException;
import al.photoBackup.exception.user.UserNameExistsException;
import al.photoBackup.exception.user.UserNameNotFoundException;
import al.photoBackup.exception.user.UserRoleNameNotFoundException;
import al.photoBackup.model.dto.user.*;
import al.photoBackup.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/users")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/id/{id}")
	public ResponseEntity<UserResponseDTO> getByIdInCompany(@PathVariable @Min(1) Integer id, Authentication authentication)
			throws UserIdNotFoundException {
		final var userDetails = (SecurityUserDetails) authentication.getPrincipal();
		final var userFound = userService.getById(id, userDetails.getCompanyId());
		if (userFound == null)
			throw new UserIdNotFoundException();
		return new ResponseEntity<>(new UserResponseDTO(userFound), HttpStatus.OK);
	}

	/**
	 * return paginated list of users in company
	 * <p>search by username if username provided is not null and not empty</p>
	 */
	@GetMapping("/in-company")
	public ResponseEntity<UserPageResponseDTO> getPaginatedInCompany(
			@RequestParam(name = "username", defaultValue = "", required = false) String name,
			@RequestParam(name = "pageNumber", defaultValue = "0") @Min(0) Integer pageNumber,
			@RequestParam(name = "pageSize", defaultValue = "20") @Min(1) Integer pageSize,
			@RequestParam(name = "sortColumn", defaultValue = "username") @Pattern(regexp = "(username|role)") String sortColumn,
			@RequestParam(name = "sortOrder", defaultValue = "desc") @Pattern(regexp = "(?i)(?:asc|desc)") String sortOrder,
			Authentication authentication
	) {
		final var userDetails = (SecurityUserDetails) authentication.getPrincipal();
		final var res = userService.searchInCompany(name, pageNumber, pageSize, sortColumn,
				sortOrder, userDetails.getCompanyId());
		return new ResponseEntity<>(res, HttpStatus.OK);
	}

	@GetMapping("/")
	@PreAuthorize("hasRole('ROLE_SYSADMIN')")
	public ResponseEntity<List<UserResponseDTO>> getAll() {
		final var response = userService.getAllActive()
				.stream()
				.map(UserResponseDTO::new)
				.collect(Collectors.toList());
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * return list of roles, except for system related
	 */
	@GetMapping("/roles")
	@PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN')")
	public ResponseEntity<List<String>> getAllRoles() {
		return new ResponseEntity<>(userService.getNonSystemRoles(), HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN')")
	public ResponseEntity<Void> disableUser(@PathVariable @Min(value = 1) Integer id, Authentication auth)
			throws UserNameNotFoundException {
		final var userDetails = (SecurityUserDetails) auth.getPrincipal();
		userService.disableUser(id, userDetails.getCompanyId());
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/")
	@PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN')")
	public ResponseEntity<UserResponseDTO> save(@Valid @RequestBody UserInsertDTO dto, Authentication auth)
			throws UserRoleNameNotFoundException, UserNameExistsException, CompanyNotFoundException {
		final var userDetails = (SecurityUserDetails) auth.getPrincipal();
		final var newUser = userService.save(dto, userDetails.getCompanyId());
		return new ResponseEntity<>(new UserResponseDTO(newUser), HttpStatus.OK);
	}

	@PutMapping("/")
	@PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN')")
	public ResponseEntity<UserResponseDTO> update(@Valid @RequestBody UserUpdateDTO dto, Authentication auth)
			throws UserRoleNameNotFoundException, UserIdNotFoundException, UserNameExistsException {
		final var userDetails = (SecurityUserDetails) auth.getPrincipal();
		final var updatedUser = userService.update(dto, userDetails.getCompanyId());
		return new ResponseEntity<>(new UserResponseDTO(updatedUser), HttpStatus.OK);
	}

	@PutMapping("/reset-password")
	public ResponseEntity<UserResponseDTO> updatePassword(@Valid @RequestBody UserPasswordDTO userPasswordDTO,
	                                                      Authentication authentication) throws WrongPasswordException {
		final var userDetails = (SecurityUserDetails) authentication.getPrincipal();
		final var updatedUser = userService.updatePassword(userDetails.getUsername(), userPasswordDTO);
		return new ResponseEntity<>(new UserResponseDTO(updatedUser), HttpStatus.OK);
	}

}