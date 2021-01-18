package org.zipli.socknet.service.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.zipli.socknet.dto.response.LoginResponse;
import org.zipli.socknet.exception.auth.AuthException;
import org.zipli.socknet.model.User;
import org.zipli.socknet.repository.UserRepository;
import org.zipli.socknet.security.jwt.JwtUtils;
import org.zipli.socknet.security.services.UserDetailsImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;

@SpringBootTest
public class AuthServiceTest {

    private final String email = "asd@gmail.com";
    private final String userName = "Vasya";
    private final String nickName = "Nick";
    private final String password = "$2a$10$ZKsGzTIrXTbap75SkIp4Oeadp4WdPoXyQ/sziesEl.wFEVmzNCQtm";
    User user = new User(email, password, userName, nickName);

    @BeforeEach
    void setUp() {
        user.setId("5ffc8765a11fd");
    }

    @Autowired
    private AuthService authService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private JwtUtils jwtUtils;
    @MockBean
    private PasswordEncoder passwordEncoder;


    @Test
    public void loginByEmailWithConfirmedEmailAndPassword_Pass() {

        Mockito.when(passwordEncoder.matches(password, user.getPassword())).thenReturn(true);

        String expected = jwtUtils.generateJwtToken(new UserDetailsImpl(user), email);
        LoginResponse expectedLoginResponse = new LoginResponse(user.getId(), expected, expected);
        user.setConfirm(true);

        Mockito.when(userRepository.findUserByEmail(email)).thenReturn(user);
        LoginResponse actualLoginResponse = authService.login(email, password);


        assertEquals(expectedLoginResponse.getUserId(), actualLoginResponse.getUserId());
        assertEquals(expectedLoginResponse.getAccessToken(), actualLoginResponse.getAccessToken());
        assertEquals(expectedLoginResponse.getRefreshToken(), actualLoginResponse.getRefreshToken());
    }

    @Test
    public void loginByEmailWithConfirmedEmailAndPassword_Fail() {
        User user = new User("differentEmail@gmail.com", password, userName, nickName);
        user.setConfirm(true);

        Mockito.when(userRepository.findUserByEmail("differentEmail@gmail.com")).thenReturn(user);

        assertThrows(AuthException.class, () -> authService.login(email, password), "User does not exist!");
    }

    @Test
    public void loginByUsernameWithConfirmedEmailAndPassword_Pass() {
        Mockito.when(passwordEncoder.matches(password, user.getPassword())).thenReturn(true);

        String expected = jwtUtils.generateJwtToken(new UserDetailsImpl(user), email);
        LoginResponse expectedLoginResponse = new LoginResponse(user.getId(), expected, expected);
        user.setConfirm(true);

        Mockito.when(userRepository.findUserByUserName(userName)).thenReturn(user);
        LoginResponse actualLoginResponse = authService.login(userName, password);

        assertEquals(expectedLoginResponse.getUserId(), actualLoginResponse.getUserId());
        assertEquals(expectedLoginResponse.getAccessToken(), actualLoginResponse.getAccessToken());
        assertEquals(expectedLoginResponse.getRefreshToken(), actualLoginResponse.getRefreshToken());
    }

    @Test
    public void loginByUsernameWithConfirmedEmailAndPassword_Fail() {
        User user = new User(email, "differentPassword", userName, nickName);
        user.setConfirm(true);

        Mockito.when(userRepository.findUserByUserName(userName)).thenReturn(null);

        assertThrows(AuthException.class, () -> authService.login(userName, password), "User does not exist!");
    }

    @Test
    public void loginByEmailWithUnconfirmedEmail_Fail() {
        Mockito.when(userRepository.findUserByEmail(email)).thenReturn(user);

        assertThrows(AuthException.class, () -> authService.login(email, password), "User does not pass email confirmation!");
    }

    @Test
    public void loginByUsernameWithUnconfirmedEmail_Fail() {
        Mockito.when(userRepository.findUserByEmail(email)).thenReturn(user);

        assertThrows(AuthException.class, () -> authService.login(email, password), "User does not pass email confirmation!");
    }

    @Test
    public void loginByUsernameWithCorrectPassword_Pass(){
        Mockito.when(userRepository.findUserByUserName(userName)).thenReturn(user);
        Mockito.when(passwordEncoder.matches(password, user.getPassword())).thenReturn(true);
        user.setConfirm(true);

        String token = jwtUtils.generateJwtToken(new UserDetailsImpl(user), email);
        LoginResponse expectedLoginResponse = new LoginResponse(user.getId(), token, token);

        LoginResponse actualLoginResponse = authService.login(userName, password);

        assertEquals(expectedLoginResponse.getUserId(), actualLoginResponse.getUserId());
        assertEquals(expectedLoginResponse.getAccessToken(), actualLoginResponse.getAccessToken());
        assertEquals(expectedLoginResponse.getRefreshToken(), actualLoginResponse.getRefreshToken());
    }

    @Test
    public void loginByUsernameWithCorrectPassword_Fail(){
        Mockito.when(userRepository.findUserByUserName(userName)).thenReturn(user);
        Mockito.when(passwordEncoder.matches(password, user.getPassword())).thenReturn(false);

        assertThrows(AuthException.class, () -> authService.login(email, password), "User entered an incorrect password");

    }



    @Test
    public void registration_Pass() {
        Mockito.when(userRepository.getUserByEmail(user.getEmail())).thenReturn(null);
        authService.registration(user);

        Mockito.verify(userRepository, times(1)).save(Mockito.any(User.class));
    }

    @Test
    public void registration_Fail() {
        Mockito.when(userRepository.getUserByEmail(user.getEmail())).thenReturn(user);

        assertThrows(AuthException.class, () -> authService.registration(user), "This email already exists!");
    }
}
