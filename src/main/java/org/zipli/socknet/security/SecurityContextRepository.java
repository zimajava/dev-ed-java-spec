package org.zipli.socknet.security;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.zipli.socknet.security.jwt.AuthTokenManager;
import reactor.core.publisher.Mono;

@Component
public class SecurityContextRepository implements ServerSecurityContextRepository {

    private final AuthTokenManager authenticationManager;

    public SecurityContextRepository(AuthTokenManager authTokenManager) {
        this.authenticationManager = authTokenManager;
    }

    @Override
    public Mono<Void> save(ServerWebExchange serverWebExchange, SecurityContext securityContext) {
        throw new IllegalStateException("Save method not supported.");
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange serverWebExchange) {
        String authHeader = serverWebExchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        String authQueryParams = serverWebExchange.getRequest()
                .getQueryParams()
                .getFirst("token");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String authToken = authHeader.substring(7);
            return getSecurityContext(authToken);
        } else if (authQueryParams != null) {
            return getSecurityContext(authQueryParams);
        }
        return Mono.empty();
    }

    private Mono<SecurityContext> getSecurityContext(String authToken) {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(authToken, authToken);
        return authenticationManager
                .authenticate(auth)
                .map(SecurityContextImpl::new);
    }
}
