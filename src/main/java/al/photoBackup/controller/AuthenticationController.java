package al.photoBackup.controller;

import al.photoBackup.exception.user.UserNameNotFoundException;
import al.photoBackup.model.dto.auth.AuthResponse;
import al.photoBackup.model.dto.user.UserAuthenticationRequest;
import al.photoBackup.service.UserService;
import al.photoBackup.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Validated
@RestController
public class AuthenticationController {
	private final AuthenticationManager authenticationManager;
	private final UserService userService;
	private final JwtUtil jwtUtil;

	public AuthenticationController(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
	                                UserService userService) {
		this.authenticationManager = authenticationManager;
		this.userService = userService;
		this.jwtUtil = jwtUtil;
	}

	/**
	 * generate jwt token if auth credentials are valid, else return custom error
	 * <p>add notification if failed to login as protected username in config</p>
	 * <p>send email notification if failed to login if configured, save error notification if email failed</p>
	 *
	 * @param authenticationRequest username and password for login
	 * @return jwt token
	 */
	@PostMapping("/authenticate")
	public ResponseEntity<AuthResponse> generateToken(@Valid @RequestBody UserAuthenticationRequest authenticationRequest)
			throws UserNameNotFoundException {
		Authentication authentication;
		authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(),
						authenticationRequest.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication); //update security context
		final var jwtDTO = jwtUtil.generateJwtToken(authentication.getName());
		final var userDetails = (UserDetails) authentication.getPrincipal();
		final var userEntity = userService.getByUsername(authentication.getName());
		//return jwt token with user details
		return ResponseEntity.ok(new AuthResponse(jwtDTO.getToken(), userEntity.getId(), userDetails.getUsername(),
				jwtDTO.getTokenExpiration()));
	}

}
