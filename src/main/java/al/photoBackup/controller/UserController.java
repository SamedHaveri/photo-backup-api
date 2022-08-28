package al.photoBackup.controller;


import al.photoBackup.exception.auth.WrongPasswordException;
import al.photoBackup.exception.user.UserIdNotFoundException;
import al.photoBackup.exception.user.UserNameExistsException;
import al.photoBackup.exception.user.UserNameNotFoundException;
import al.photoBackup.model.dto.user.SecurityUserDetails;
import al.photoBackup.model.dto.user.UserPasswordDTO;
import al.photoBackup.model.dto.user.UserResponseDTO;
import al.photoBackup.model.dto.user.UserUpdateDTO;
import al.photoBackup.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Validated
@RestController
@RequestMapping("/user")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@DeleteMapping("/disable")
	public ResponseEntity<Void> disableUser(Authentication auth)
			throws UserNameNotFoundException {
		final var userDetails = (SecurityUserDetails) auth.getPrincipal();
		userService.disableUser(userDetails.getId());
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PutMapping("/")
	public ResponseEntity<UserResponseDTO> update(@Valid @RequestBody UserUpdateDTO dto, Authentication auth)
			throws UserIdNotFoundException, UserNameExistsException {
		final var userDetails = (SecurityUserDetails) auth.getPrincipal();
		final var updatedUser = userService.update(dto, userDetails.getId());
		return new ResponseEntity<>(new UserResponseDTO(updatedUser), HttpStatus.OK);
	}

	@PutMapping("/reset-password")
	public ResponseEntity<UserResponseDTO> updatePassword(@Valid @RequestBody UserPasswordDTO userPasswordDTO,
	                                                      Authentication authentication)
			throws WrongPasswordException,UserNameNotFoundException {
		final var userDetails = (SecurityUserDetails) authentication.getPrincipal();
		final var updatedUser = userService.updatePassword(userDetails.getUsername(), userPasswordDTO);
		return new ResponseEntity<>(new UserResponseDTO(updatedUser), HttpStatus.OK);
	}

}