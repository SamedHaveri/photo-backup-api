package al.photoBackup.util;

import al.photoBackup.config.JwtConfig;
import al.photoBackup.model.dto.auth.TokenDTO;
import io.jsonwebtoken.*;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;

@Service
public class JwtUtil {
	private final SecretKey secretKey;
	private final JwtConfig jwtConfig;

	public JwtUtil(SecretKey secretKey, JwtConfig jwtConfig) {
		this.secretKey = secretKey;
		this.jwtConfig = jwtConfig;
	}

	public TokenDTO generateJwtToken(String username) {
		final var expirationDateTime = LocalDateTime.now().plusDays(jwtConfig.getTokenExpirationAfterDays());
		final var token =  Jwts.builder()
				.setSubject(username)
				.setIssuedAt(new Date())
				.setExpiration(java.sql.Date.valueOf(expirationDateTime.toLocalDate()))
				.signWith(secretKey, SignatureAlgorithm.HS512)
				.compact();
		return new TokenDTO(token, expirationDateTime);
	}

	public String getUserNameFromJwtToken(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(secretKey)
				.build()
				.parseClaimsJws(token)
				.getBody()
				.getSubject();
	}

	public boolean validateJwtToken(String authToken) {
		try {
			Jwts.parserBuilder()
					.setSigningKey(secretKey)
					.build()
					.parseClaimsJws(authToken);
			return true;
		} catch (SignatureException e) {
			System.out.println("Invalid JWT signature: " + e.getMessage());
		} catch (MalformedJwtException e) {
			System.out.println("Invalid JWT token: " + e.getMessage());
		} catch (ExpiredJwtException ignored) {
		} catch (UnsupportedJwtException e) {
			System.out.println("JWT token is unsupported: " + e.getMessage());
		} catch (IllegalArgumentException e) {
			System.out.println("JWT claims string is empty: " + e.getMessage());
		}
		return false;
	}
}
