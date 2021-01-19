package org.zipli.socknet.security;

import io.netty.handler.codec.http.HttpMethod;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.zipli.socknet.security.jwt.AuthTokenManager;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
//@PropertySource("")
public class WebSecurityConfig {
    @Value("${cors.urls}")
    private List<String> corsUrls;

    @Value("${cors.path}")
    private String corsPath;

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
                .pathMatchers("/zipli/auth/signup",
                        "/zipli/auth/signin",
                        "/zipli/auth/forgot_password",
                        "/v2/api-docs",
                        "/configuration/ui",
                        "/swagger-resources/**",
                        "/configuration/security",
                        "/swagger-ui/**",
                        "/webjars/**").permitAll()
                .anyExchange().authenticated()
//                .and().oauth2Client()
                .and().build();
    }

    @Bean
    CorsConfigurationSource corsConfiguration() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.applyPermitDefaultValues();
        corsConfig.addAllowedMethod(String.valueOf(HttpMethod.PUT));
        corsConfig.addAllowedMethod(String.valueOf(HttpMethod.DELETE));
        corsConfig.setAllowedOrigins(corsUrls);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(corsPath, corsConfig);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
