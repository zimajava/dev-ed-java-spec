package org.zipli.socknet.security.jwt;


import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.zipli.socknet.security.services.UserDetailsImpl;

import java.util.Date;

@Component
@Slf4j
public class JwtUtils {

	@Value("${app.jwtSecret}")
	private String jwtSecret;
	@Value("${app.jwtExpirationMs}")
	private int jwtExpirationMs;

	public String generateJwtToken(UserDetails userPrincipal) {
		return Jwts.builder()
				.setSubject((userPrincipal.getUsername()))
				.setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
				.signWith(SignatureAlgorithm.HS512, jwtSecret)
				.compact();
	}

	public String getUserNameFromJwtToken(String token) {
		return Jwts.parser()
				.setSigningKey(jwtSecret)
				.parseClaimsJws(token)
				.getBody()
				.getSubject();
	}

	public boolean validateJwtToken(String authToken) {
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
			return true;
		} catch (SignatureException e) {
			log.error("Invalid JWT signature: {}", e.getMessage());
		} catch (Exception e){
			log.error("JWT exception: {}", e.getMessage());
		}

		return false;
	}
}
