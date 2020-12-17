package org.zipli.socknet.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.zipli.socknet.exception.*;
import org.zipli.socknet.model.User;
import org.zipli.socknet.payload.request.LoginRequest;
import org.zipli.socknet.payload.request.SignupRequest;
import org.zipli.socknet.repository.UserRepository;
import org.zipli.socknet.security.jwt.JwtUtils;
import org.zipli.socknet.service.email.EmailConfirmationService;
import org.zipli.socknet.service.password.ResetPasswordService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuthControllerTest {
    @Autowired
    AuthController authController;
    @MockBean
    EmailConfirmationService emailConfirmationService;
    @MockBean
    ResetPasswordService resetPasswordService;
    @MockBean
    JavaMailSender javaMailSender;
    @MockBean
    UserRepository userRepository;
    @MockBean
    JwtUtils jwtUtils;
    private LoginRequest loginRequest;
    private SignupRequest signupRequest;

//    @MockBean
//    LoginRequest loginRequest1;
    private SignupRequest signupRequest1;

    @BeforeEach
    public void init() {
        signupRequest = new SignupRequest(
                "newUser@gmail.com",
                "ugyur",
                "uyfrjjj",
                "gfr53");

        signupRequest1 = new SignupRequest(
                "registeredUser@gmail.com",
                "ugyur",
                "uyfrjjj",
                "gfr53");

        loginRequest = new LoginRequest("ugyur",
                "uyfrjjj");
    }

    @Test
    void addUser_UserIsOk() {

        assertEquals(authController.addUser(signupRequest), ResponseEntity.ok("User registered successfully!"));
    }

    @Test
    void addUser_UserHasAlreadyRegistered() {
        Mockito.doReturn(new User())
                .when(userRepository)
                .getUserByEmail("registeredUser@gmail.com");
        AuthException e = new AuthException("This email already exists!");

        assertNotEquals(authController.addUser(signupRequest1), ResponseEntity.badRequest()
                .body(e));
    }

    @Test
    void addUser_NotValidValues() {
        Mockito.doReturn(null)
                .when(userRepository)
                .getUserByEmail("registeredUser@gmail.com");

        assertNotEquals(authController.addUser(signupRequest1), ResponseEntity.badRequest()
                .body("Not valid values"));
    }

    @Test
    void emailConfirm_TokenIsValid() {
        String token = "qwerty";
        String username = "";
        Mockito.doReturn(username)
                .when(jwtUtils)
                .getUserNameFromJwtToken(token);
        Mockito.doReturn(new User())
                .when(userRepository)
                .getByUserName(username);

        assertEquals(authController.emailConfirm(token), ResponseEntity.ok("Account verified"));
    }

    @Test
    void emailConfirm_TokenIsInvalid() {
        Mockito.doThrow(new NotConfirmAccountException("Error. The token is invalid or broken!"))
                .when(emailConfirmationService)
                .confirmAccount(null);
        NotConfirmAccountException e = new NotConfirmAccountException("Error. The token is invalid or broken!");

        assertNotEquals(ResponseEntity
                        .badRequest()
                        .body(e),
                authController.emailConfirm(null));
    }

    @Test
    void processForgotPassword_UserIsRegisteredInDatabase(){
      String email = "registeredUser@gmail.com";
      String token = new String();
        Mockito.doReturn(token)
                .when(resetPasswordService)
                .generateResetPasswordToken(email);

        assertEquals(authController.processForgotPassword(email),
                ResponseEntity.ok("Password can be changed"));
    }

    @Test
    void processForgotPassword_UserIsNotFound(){
        String email = "kh;uifyd";
        Mockito.doThrow(new UserNotFoundException("Error. User is not founded."))
                .when(resetPasswordService)
                .generateResetPasswordToken(email);
        UserNotFoundException e = new UserNotFoundException("Error. User is not founded.");

        assertNotEquals(ResponseEntity
                        .badRequest()
                        .body(e),
                authController.processForgotPassword(email));
    }

    @Test
    void processResetPassword_TokenIsValid(){
        String newPassword = "jvtiyd4218";
        String changedPassword = new String();
        String token = "hjvftf";
        Mockito.doReturn(changedPassword)
                .when(resetPasswordService)
                .resetPassword(newPassword,token);

        assertEquals(authController.processResetPassword(token, newPassword),
                ResponseEntity.ok("Password successfully changed"));
    }

    @Test
    void processResetPassword_TokenIsInvalid(){
        String newPassword = "jvtiyd4218";
        String token = "hjvftf";
        Mockito.doThrow(new InvalidTokenException("Error. Token is invalid or broken"))
                .when(resetPasswordService)
                .resetPassword(newPassword,token);
        InvalidTokenException e = new InvalidTokenException("Error. Token is invalid or broken");

        assertNotEquals(ResponseEntity
                        .badRequest()
                        .body(e),
                authController.processResetPassword(token, newPassword));
    }


    @Test
    void authenticateUser_shouldReturnStatusOk() {
        assertTrue(authController.authenticateUser(loginRequest)
                .equals(ResponseEntity.ok("Here will be JwtResponse")));

    }
}
