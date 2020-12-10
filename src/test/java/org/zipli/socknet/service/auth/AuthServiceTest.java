package org.zipli.socknet.service.auth;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.zipli.socknet.exception.AuthException;
import org.zipli.socknet.model.User;
import org.zipli.socknet.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void loginByEmailAndPasswordWithConfirmedEmail_Pass() {
        String email = "asd@gmail.com";
        String userName = "Vasya";
        String password = "12345";
        User user = new User(email, password, userName, "Cat");
        user.setConfirm(true);

        Mockito.when(userRepository.findUserByEmailAndPassword(email, password)).thenReturn(user);

        assertEquals(user, authService.login(email, password));
    }

    @Test
    public void loginByEmailAndPasswordWithConfirmedEmail_Fail() {
        String email = "asd@gmail.com";
        String userName = "Vasya";
        String password = "12345";
        User user = new User("differentEmail@gmail.com", password, userName, "Cat");
        user.setConfirm(true);

        Mockito.when(userRepository.findUserByEmailAndPassword("differentEmail@gmail.com", password)).thenReturn(user);

        assertThrows(AuthException.class, () -> authService.login(email, password), "User does not exist!");
    }

    @Test
    public void loginByUsernameAndPasswordWithConfirmedEmail_Pass() {
        String email = "asd@gmail.com";
        String userName = "Vasya";
        String password = "12345";
        User user = new User(email, password, userName, "Cat");
        user.setConfirm(true);

        Mockito.when(userRepository.findUserByUserNameAndPassword(userName, password)).thenReturn(user);

        assertEquals(user, authService.login(userName, password));
    }

    @Test
    public void loginByUsernameAndPasswordWithConfirmedEmail_Fail() {
        String email = "asd@gmail.com";
        String userName = "Vasya";
        String password = "12345";
        User user = new User(email, "differentPassword", userName, "Cat");
        user.setConfirm(true);

        Mockito.when(userRepository.findUserByUserNameAndPassword(userName, "differentPassword")).thenReturn(user);

        assertThrows(AuthException.class, () -> authService.login(email, password), "User does not exist!");
    }

    @Test
    public void loginByEmailAndPasswordWithUnconfirmedEmail_Fail() {
        String email = "asd@gmail.com";
        String userName = "Vasya";
        String password = "12345";
        User user = new User(email, password, userName, "Cat");

        Mockito.when(userRepository.findUserByEmailAndPassword(email, password)).thenReturn(user);

        assertThrows(AuthException.class, () -> authService.login(email, password), "User does not pass email confirmation!");
    }

    @Test
    public void loginByUsernameAndPasswordWithUnconfirmedEmail_Fail() {
        String email = "asd@gmail.com";
        String userName = "Vasya";
        String password = "12345";
        User user = new User(email, password, userName, "Cat");

        Mockito.when(userRepository.findUserByEmailAndPassword(userName, password)).thenReturn(user);

        assertThrows(AuthException.class, () -> authService.login(userName, password), "User does not pass email confirmation!");
    }

}
