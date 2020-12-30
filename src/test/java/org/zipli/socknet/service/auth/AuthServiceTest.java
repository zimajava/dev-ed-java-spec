package org.zipli.socknet.service.auth;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.zipli.socknet.dto.response.LoginResponse;
import org.zipli.socknet.exception.AuthException;
import org.zipli.socknet.model.User;
import org.zipli.socknet.repository.UserRepository;
import org.zipli.socknet.security.jwt.JwtUtils;
import org.zipli.socknet.security.services.UserDetailsImpl;

import javax.mail.MessagingException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;

@SpringBootTest
public class AuthServiceTest {

    private final String email = "asd@gmail.com";
    private final String userName = "Vasya";
    private final String password = "12345";
    @Autowired
    private AuthService authService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private JwtUtils jwtUtils;

    @Test
    public void loginByEmailAndPasswordWithConfirmedEmail_Pass() {
        User user = new User(email, password, userName, "Cat");
        String expected = jwtUtils.generateJwtToken(new UserDetailsImpl(user), email);
        LoginResponse expectedLoginResponse = new LoginResponse(user.getId(), expected, expected);
        user.setConfirm(true);

        Mockito.when(userRepository.findUserByEmailAndPassword(email, password)).thenReturn(user);
        LoginResponse actualLoginResponse = authService.login(email, password);


        assertEquals(expectedLoginResponse.getUserId(), actualLoginResponse.getUserId());
        assertEquals(expectedLoginResponse.getAccessToken(), actualLoginResponse.getAccessToken());
        assertEquals(expectedLoginResponse.getRefreshToken(), actualLoginResponse.getRefreshToken());
    }

    @Test
    public void loginByEmailAndPasswordWithConfirmedEmail_Fail() {
        User user = new User("differentEmail@gmail.com", password, userName, "Cat");
        user.setConfirm(true);

        Mockito.when(userRepository.findUserByEmailAndPassword("differentEmail@gmail.com", password)).thenReturn(user);

        assertThrows(AuthException.class, () -> authService.login(email, password), "User does not exist!");
    }

    @Test
    public void loginByUsernameAndPasswordWithConfirmedEmail_Pass() {
        User user = new User(email, password, userName, "Cat");
        String expected = jwtUtils.generateJwtToken(new UserDetailsImpl(user), email);
        LoginResponse expectedLoginResponse = new LoginResponse(user.getId(), expected, expected);
        user.setConfirm(true);

        Mockito.when(userRepository.findUserByUserNameAndPassword(userName, password)).thenReturn(user);
        LoginResponse actualLoginResponse = authService.login(userName, password);


        assertEquals(expectedLoginResponse.getUserId(), actualLoginResponse.getUserId());
        assertEquals(expectedLoginResponse.getAccessToken(), actualLoginResponse.getAccessToken());
        assertEquals(expectedLoginResponse.getRefreshToken(), actualLoginResponse.getRefreshToken());
    }

    @Test
    public void loginByUsernameAndPasswordWithConfirmedEmail_Fail() {
        User user = new User(email, "differentPassword", userName, "Cat");
        user.setConfirm(true);

        Mockito.when(userRepository.findUserByUserNameAndPassword(userName, "differentPassword")).thenReturn(user);

        assertThrows(AuthException.class, () -> authService.login(email, password), "User does not exist!");
    }

    @Test
    public void loginByEmailAndPasswordWithUnconfirmedEmail_Fail() {
        User user = new User(email, password, userName, "Cat");

        Mockito.when(userRepository.findUserByEmailAndPassword(email, password)).thenReturn(user);

        assertThrows(AuthException.class, () -> authService.login(email, password), "User does not pass email confirmation!");
    }

    @Test
    public void loginByUsernameAndPasswordWithUnconfirmedEmail_Fail() {
        User user = new User(email, password, userName, "Cat");

        Mockito.when(userRepository.findUserByEmailAndPassword(userName, password)).thenReturn(user);

        assertThrows(AuthException.class, () -> authService.login(userName, password), "User does not pass email confirmation!");
    }

    @Test
    public void registration_Pass() {
        User user = new User(email, password, userName, "Cat");

        Mockito.when(userRepository.getUserByEmail(user.getEmail())).thenReturn(null);
        authService.registration(user);

        Mockito.verify(userRepository, times(1)).save(Mockito.any(User.class));
    }

    @Test
    public void registration_Fail() {
        User user = new User(email, password, userName, "Cat");

        Mockito.when(userRepository.getUserByEmail(user.getEmail())).thenReturn(user);

        assertThrows(AuthException.class, () -> authService.registration(user), "This email already exists!");
    }
}
