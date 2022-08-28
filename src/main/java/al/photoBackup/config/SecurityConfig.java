package al.photoBackup.config;

import al.photoBackup.security.filter.JwtAuthenticationFilter;
import al.photoBackup.security.filter.JwtTokenVerifierFilter;
import al.photoBackup.service.UserService;
import al.photoBackup.util.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig{
	private final PasswordEncoder passwordEncoder;
	private final SecretKey secretKey;
	private final JwtConfig jwtConfig;
	private final JwtUtil jwtUtil;
	private final UserService userService;

	public SecurityConfig(PasswordEncoder passwordEncoder, SecretKey secretKey, JwtConfig jwtConfig, JwtUtil jwtUtil,
								 UserService userService) {
		this.passwordEncoder = passwordEncoder;
		this.secretKey = secretKey;
		this.jwtConfig = jwtConfig;
		this.jwtUtil = jwtUtil;
		this.userService = userService;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
		http
				.cors()
				.and()
				.csrf().disable()
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
				.addFilter(new JwtAuthenticationFilter(authenticationManager, jwtConfig, secretKey))
				.addFilterAfter(new JwtTokenVerifierFilter(jwtUtil, userService), JwtAuthenticationFilter.class)
				.authorizeRequests()
				.antMatchers("/authenticate", "/status").permitAll()
				.anyRequest()
				.authenticated();
		return http.build();
	}

	@Bean
	protected AuthenticationManager authenticationManager(AuthenticationConfiguration auth) throws Exception {
		return auth.getAuthenticationManager();
	}

	@Bean
	public AuthenticationEntryPoint authenticationEntryPoint(){
		return new CustomAuthenticationEntryPoint();
	}

}