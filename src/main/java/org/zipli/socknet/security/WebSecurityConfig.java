package org.zipli.socknet.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.zipli.socknet.security.jwt.AuthTokenManager;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
//@PropertySource("")
public class WebSecurityConfig {

    private final AuthTokenManager authTokenManager;
    private final SecurityContextRepository securityContextRepository;

    public WebSecurityConfig(AuthTokenManager authTokenManager, SecurityContextRepository securityContextRepository) {
        this.authTokenManager = authTokenManager;
        this.securityContextRepository = securityContextRepository;

    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .exceptionHandling()
                .authenticationEntryPoint(
                        (swe, e) -> Mono.fromRunnable(
                                () -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)
                        )
                )
                .and()
                .csrf().disable()
                .authenticationManager(authTokenManager)
                .securityContextRepository(securityContextRepository)
                .authorizeExchange()
                .pathMatchers("/zipli/auth/**").permitAll()
                .anyExchange().authenticated()
//                .and().oauth2Client()
                .and().build();
    }

}
