package org.zipli.socknet.security;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.zipli.socknet.dto.response.LoginResponse;
import org.zipli.socknet.exception.AuthException;
import org.zipli.socknet.model.User;
import org.zipli.socknet.repository.UserRepository;
import org.zipli.socknet.security.jwt.AuthTokenManager;
import org.zipli.socknet.security.jwt.JwtUtils;
import org.zipli.socknet.security.services.UserDetailsImpl;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class SecurityContextRepository implements ServerSecurityContextRepository {

    private final AuthTokenManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    public SecurityContextRepository(AuthTokenManager authTokenManager, UserRepository userRepository, JwtUtils jwtUtils) {
        this.authenticationManager = authTokenManager;
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public Mono<Void> save(ServerWebExchange serverWebExchange, SecurityContext securityContext) {
        User user;

        DefaultOidcUser userPrincipal = (DefaultOidcUser)securityContext.getAuthentication().getPrincipal();
        Map<String, Object> userInfo = userPrincipal.getUserInfo().getClaims();
        String email = (String)userInfo.get("email");
        String nickName = (String)userInfo.get("name");

        user = userRepository.findUserByEmail(email);
        if (user == null) {
            user = new User(email, nickName, true, true);
            userRepository.save(user);
        }

            String token = jwtUtils.generateJwtToken(new UserDetailsImpl(user), user.getEmail());
//            return new LoginResponse(user.getId(), token, token);

        return Mono.empty();
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
