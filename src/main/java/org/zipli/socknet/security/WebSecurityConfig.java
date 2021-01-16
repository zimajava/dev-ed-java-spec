package org.zipli.socknet.security;

import io.netty.handler.codec.http.HttpMethod;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.client.WebClient;
import org.zipli.socknet.security.jwt.AuthTokenManager;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
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
                //.exceptionHandling() - не работает с фгер
//                .authenticationEntryPoint(
//                        (swe, e) -> Mono.fromRunnable(
//                                () -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)
//                        )
//                )
//                .and()
                .csrf().disable()
                .authenticationManager(authTokenManager)
                .securityContextRepository(securityContextRepository)
                .authorizeExchange()
                .pathMatchers("/zipli/auth/**", "/v2/api-docs",
                        "/configuration/ui",
                        "/swagger-resources/**",
                        "/configuration/security",
                        "/swagger-ui/**",
                        "/webjars/**", "/").permitAll()
                .anyExchange().authenticated()
                .and().oauth2Login()
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

//    @Bean
//    WebClient webClient(
//            ReactiveClientRegistrationRepository clientRegistrations,
//            ServerOAuth2AuthorizedClientRepository authorizedClients) {
//        ServerOAuth2AuthorizedClientExchangeFilterFunction oauth =
//                new ServerOAuth2AuthorizedClientExchangeFilterFunction(
//                        clientRegistrations,
//                        authorizedClients);
//        oauth.setDefaultOAuth2AuthorizedClient(true);
//        return WebClient.builder()
//                .filter(oauth)
//                .build();
//    }
}
