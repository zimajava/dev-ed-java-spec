package org.zipli.socknet.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.zipli.socknet.exception.AuthException;
import org.zipli.socknet.model.User;
import org.zipli.socknet.repository.UserRepository;
import org.zipli.socknet.service.auth.AuthService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;

@SpringBootTest
public class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @MockBean
    private UserRepository userRepository;

    private final String email = "asd@gmail.com";
    private final String userName = "Vasya";
    private final String password = "12345";

    @Test
    public void loginByEmailAndPasswordWithConfirmedEmail_Pass() {
        User user = new User(email, password, userName, "Cat");
        user.setConfirm(true);

        Mockito.when(userRepository.findUserByEmailAndPassword(email, password)).thenReturn(user);

        assertEquals(user, authService.login(email, password));
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
        user.setConfirm(true);

        Mockito.when(userRepository.findUserByUserNameAndPassword(userName, password)).thenReturn(user);

        assertEquals(user, authService.login(userName, password));
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
