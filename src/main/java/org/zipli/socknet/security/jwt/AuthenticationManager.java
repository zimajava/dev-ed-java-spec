package org.zipli.socknet.security.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.zipli.socknet.exception.AuthenticationException;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class AuthenticationManager implements ReactiveAuthenticationManager {
    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    public AuthenticationManager(JwtUtils jwtUtils, @Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }


    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToken = authentication.getCredentials().toString();

        String username;

        if (authToken != null && jwtUtils.validateJwtToken(authToken)) {
            username = jwtUtils.getUserNameFromJwtToken(authToken);

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            log.info("User authentication");
            return Mono.just(authenticationToken);
        }else {
            log.info("Cannot set user authentication: no valid Jwt token");
            throw new AuthenticationException();
        }
    }
}
