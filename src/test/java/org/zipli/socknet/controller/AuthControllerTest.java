package org.zipli.socknet.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.zipli.socknet.dto.response.LoginResponse;
import org.zipli.socknet.exception.*;
import org.zipli.socknet.model.User;
import org.zipli.socknet.payload.request.LoginRequest;
import org.zipli.socknet.payload.request.SignupRequest;
import org.zipli.socknet.repository.UserRepository;
import org.zipli.socknet.security.jwt.JwtUtils;
import org.zipli.socknet.security.services.UserDetailsImpl;
import org.zipli.socknet.service.auth.AuthService;
import org.zipli.socknet.service.email.EmailConfirmationService;
import org.zipli.socknet.service.password.ResetPasswordService;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class AuthControllerTest {

    @Autowired
    AuthController authController;
    @MockBean
    EmailConfirmationService emailConfirmationService;
    @MockBean
    AuthService authService;
    @MockBean
    ResetPasswordService resetPasswordService;
    @MockBean
    JavaMailSender javaMailSender;
    @MockBean
    UserRepository userRepository;
    @MockBean
    JwtUtils jwtUtils;

    private LoginRequest loginRequest;
    private LoginRequest validRequest;
    private LoginResponse loginResponse;
    private SignupRequest signupRequest;
    private SignupRequest signupRequest1;
    private String newPassword;
    private String token;
    private String email;

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

        validRequest = new LoginRequest("new23User@gmail.com",
                "dsfh78Kjhve");
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
        AuthException e = new AuthException(ErrorStatusCode.EMAIL_ALREADY_EXISTS);

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
        String username = "";
        Mockito.doReturn(username)
                .when(jwtUtils)
                .getUserNameFromJwtToken(token);
        Mockito.doReturn(new User())
                .when(userRepository)
                .getUserByUserName(username);

        assertEquals(authController.emailConfirm(token), ResponseEntity.ok("Account verified"));
    }

    @Test
    void emailConfirm_TokenIsInvalid() {
        Mockito.doThrow(new NotConfirmAccountException(ErrorStatusCode.TOKEN_INVALID_OR_BROKEN))
                .when(emailConfirmationService)
                .confirmAccount(null);
        NotConfirmAccountException e = new NotConfirmAccountException(ErrorStatusCode.TOKEN_INVALID_OR_BROKEN);

        assertNotEquals(ResponseEntity
                        .badRequest()
                        .body(e),
                authController.emailConfirm(null));
    }

    @Test
    void processForgotPassword_UserIsRegisteredInDatabase() {
        String email = "registeredUser@gmail.com";

        assertEquals(ResponseEntity.ok("Password can be changed"),
                authController.processForgotPassword(email));
    }

    @Test
    void processForgotPassword_UserIsNotFound() {

        Mockito.doThrow(new UserNotFoundException(ErrorStatusCode.EMAIL_DOES_NOT_CORRECT))
                .when(resetPasswordService).generateResetPasswordToken(email);

        assertEquals(ResponseEntity
                .badRequest()
                .body(ErrorStatusCode.EMAIL_DOES_NOT_CORRECT.getValue()),
                authController.processForgotPassword(email));
    }

    @Test
    void processResetPassword_TokenIsValid() {
        String newPassword = "ugyur2Wa4";
        User user = new User("gmail.com", "kRol1", "userName", "nick");
        String username = user.getUserName();

        Mockito.doReturn(user)
                .when(userRepository)
                .getUserByUserName(username);

        Mockito.doReturn(token)
                .when(jwtUtils)
                .generateJwtToken(new UserDetailsImpl(user), user.getEmail());

        assertEquals(ResponseEntity.ok("Password successfully changed"),
                authController.processResetPassword(token, newPassword));
    }

    @Test
    void processResetPassword_NullParameter() {

        Mockito.doThrow(new UserNotFoundException(ErrorStatusCode.USER_DOES_NOT_EXIST))
                .when(resetPasswordService).resetPassword(token, newPassword);

        assertEquals(ResponseEntity
                .badRequest()
                .body(ErrorStatusCode.USER_DOES_NOT_EXIST.getValue()),
                authController.processResetPassword(token, newPassword));
    }

    @Test
    void authenticateUser_shouldReturnStatusOk() {
        Mockito.doReturn(loginResponse)
                .when(authService)
                .login(validRequest.getLogin(), validRequest.getPassword());

        assertEquals(ResponseEntity.ok()
                .body(loginResponse), authController.authenticateUser(validRequest));
    }

    @Test
    void authenticateUser_shouldReturnBadRequest() {

        Mockito.doThrow(new AuthException(ErrorStatusCode.USER_DOES_NOT_EXIST))
                .when(authService)
                .login(loginRequest.getLogin(), loginRequest.getPassword());

        assertEquals(ResponseEntity.badRequest()
                .body(ErrorStatusCode.USER_DOES_NOT_EXIST.getValue()), authController.authenticateUser(loginRequest));
    }
}
