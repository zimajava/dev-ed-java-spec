package org.zipli.socknet.payload.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class LoginRequestTest {
    private final Pattern emailPattern = Pattern.compile("^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*\\.\\w{2,}$");
    private final Pattern passwordPattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,16}$");
    private final Pattern userNamePattern = Pattern.compile("^[a-zA-Z0-9_-]{8,16}$");

    private LoginRequest validParametersEmail;
    private LoginRequest validParametersUserName;
    private LoginRequest emptyLoginRequestEmail;
    private LoginRequest emptyLoginRequestUserName;
    private LoginRequest fieldsLessThan8Email;
    private LoginRequest fieldsLessThan8UserName;
    private LoginRequest fieldsMoreThan16Email;
    private LoginRequest fieldsMoreThan16UserName;
    private LoginRequest fieldsWithoutUpperCaseEmail;
    private LoginRequest fieldsWithoutUpperCaseUserName;
    private LoginRequest fieldsWithoutLowerCaseEmail;
    private LoginRequest fieldsWithoutLowerCaseUserName;
    private LoginRequest fieldsWithSpacesEmail;
    private LoginRequest fieldsWithSpacesUserName;
    private LoginRequest fieldsWithSpecificCharsEmail;
    private LoginRequest fieldsWithSpecificCharsUserName;
    private LoginRequest fieldsWithTwoDogs;

    @BeforeEach
    public void init() {
        validParametersEmail = new LoginRequest(
                "newUser2@gmail.com",
                "ugyur2Wa4");

        validParametersUserName = new LoginRequest(
                "uyfrjjj3Jk8",
                "ugyur2Wa4");

        emptyLoginRequestEmail = new LoginRequest("", "");

        emptyLoginRequestUserName = new LoginRequest("", "");

        fieldsLessThan8Email = new LoginRequest(
                "newUser2@gmail.c",
                "ugyur3H");

        fieldsLessThan8UserName = new LoginRequest(
                "uyHrjj5",
                "ugyur3H");

        fieldsMoreThan16Email = new LoginRequest(
                "ewUnjnoe899249mlfser2@gmail.com",
                "ugyur2Wa4sdjkfjkskllsllj");

        fieldsMoreThan16UserName = new LoginRequest(
                "uyfrjjj3Jk8sfkdkjjgdjgdl",
                "ugyur2Wa4sdjkfjkskllsllj");

        fieldsWithSpacesEmail = new LoginRequest(
                "new User2@gmail.com",
                "ugyur 2Wa4");

        fieldsWithSpacesUserName = new LoginRequest(
                "uyfrj jj3Jk8",
                "ugyur 2Wa4");

        fieldsWithoutUpperCaseEmail = new LoginRequest(
                "newuser2@gmail.com",
                "ugyur2wa4");

        fieldsWithoutUpperCaseUserName = new LoginRequest(
                "kjfdk87dfkl",
                "ugyur2wa4");

        fieldsWithoutLowerCaseEmail = new LoginRequest(
                "NEWUSER2@GMAIL.COM",
                "UGHSK2WGKK4");

        fieldsWithoutLowerCaseUserName = new LoginRequest(
                "UGHSK2WGKK4",
                "UGHSK2WGKK4");

        fieldsWithSpecificCharsEmail = new LoginRequest(
                "n_ew.j-ser2@gmail.com",
                "ugyur2Wa4");

        fieldsWithSpecificCharsUserName = new LoginRequest(
                "uyfrj_jj3-Jk8",
                "ugyur2Wa4");

        fieldsWithTwoDogs = new LoginRequest(
                "new@User2@gmail.com",
                "ugyur2Wa4");
    }

    @Test
    public void isBlank_ShouldReturnFalse() {
        assertFalse(validParametersEmail.getLogin()
                                        .isBlank());
        assertFalse(validParametersUserName.getLogin()
                                           .isBlank());
        assertFalse(validParametersEmail.getPassword()
                                        .isBlank());
        assertFalse(validParametersUserName.getPassword()
                                           .isBlank());
    }

    @Test
    public void isBlank_ShouldReturnTrue() {
        assertTrue(emptyLoginRequestEmail.getLogin()
                                         .isBlank());
        assertTrue(emptyLoginRequestUserName.getLogin()
                                            .isBlank());
        assertTrue(emptyLoginRequestEmail.getPassword()
                                         .isBlank());
        assertTrue(emptyLoginRequestUserName.getPassword()
                                            .isBlank());
    }

    @Test
    public void login_ShouldPassValidation() {
        System.out.println("email is valid : " + validParametersEmail.getLogin());
        assertTrue(validParametersEmail.getLogin()
                                       .matches(String.valueOf(emailPattern)));

        System.out.println("email is valid : " + fieldsWithoutUpperCaseEmail.getLogin());
        assertTrue(fieldsWithoutUpperCaseEmail.getLogin()
                                              .matches(String.valueOf(emailPattern)));

        System.out.println("email is valid : " + fieldsWithSpecificCharsEmail.getLogin());
        assertTrue(fieldsWithSpecificCharsEmail.getLogin()
                                               .matches(String.valueOf(emailPattern)));

        System.out.println("userName is valid : " + validParametersUserName.getLogin());
        assertTrue(validParametersUserName.getLogin()
                                          .matches(String.valueOf(userNamePattern)));

        System.out.println("userName is valid : " + fieldsWithoutLowerCaseUserName.getLogin());
        assertTrue(fieldsWithoutLowerCaseUserName.getLogin()
                                                 .matches(String.valueOf(userNamePattern)));

        System.out.println("userName is valid : " + fieldsWithoutUpperCaseUserName.getLogin());
        assertTrue(fieldsWithoutUpperCaseUserName.getLogin()
                                                 .matches(String.valueOf(userNamePattern)));

        System.out.println("userName is valid : " + fieldsWithSpecificCharsUserName.getLogin());
        assertTrue(fieldsWithSpecificCharsUserName.getLogin()
                                                  .matches(String.valueOf(userNamePattern)));
    }


    @Test
    public void login_ShouldNotPassValidation() {
        System.out.println("email contains two @ characters : " + fieldsWithTwoDogs.getLogin());
        assertFalse(fieldsWithTwoDogs.getLogin()
                                     .matches(String.valueOf(emailPattern)));

        System.out.println("email should not contain spaces : " + fieldsWithSpacesEmail.getLogin());
        assertFalse(fieldsWithSpacesEmail.getLogin()
                                         .matches(String.valueOf(emailPattern)));

        System.out.println("email should contain at least two characters after . : " + fieldsLessThan8Email.getLogin());
        assertFalse(fieldsLessThan8Email.getLogin()
                                        .matches(String.valueOf(emailPattern)));

        System.out.println("userName size is less than 8 characters : " + fieldsLessThan8UserName.getLogin());
        assertFalse(fieldsLessThan8UserName.getLogin()
                                           .matches(String.valueOf(userNamePattern)));

        System.out.println("userName size is more than 16 characters : " + fieldsMoreThan16UserName.getLogin());
        assertFalse(fieldsMoreThan16UserName.getLogin()
                                            .matches(String.valueOf(userNamePattern)));

        System.out.println("userName should not contain spaces : " + fieldsWithSpacesUserName.getLogin());
        assertFalse(fieldsWithSpacesUserName.getLogin()
                                            .matches(String.valueOf(userNamePattern)));
    }

    @Test
    public void password_ShouldPassValidation() {
        System.out.println("password is valid (login = email) : " + validParametersEmail.getPassword());
        assertTrue(validParametersEmail.getPassword()
                                       .matches(String.valueOf(passwordPattern)));

        System.out.println("password is valid (login = username) : " + validParametersUserName.getPassword());
        assertTrue(validParametersUserName.getPassword()
                                          .matches(String.valueOf(passwordPattern)));
    }

    @Test
    public void password_ShouldNotPassValidation() {
        System.out.println("password size is less than 8 characters : " + fieldsLessThan8Email.getPassword());
        assertFalse(fieldsLessThan8Email.getPassword()
                                        .matches(String.valueOf(passwordPattern)));

        System.out.println("password size is more than 16 characters : " + fieldsMoreThan16Email.getPassword());
        assertFalse(fieldsMoreThan16Email.getPassword()
                                         .matches(String.valueOf(passwordPattern)));

        System.out.println("password should contain at least one UpperCase character : " + fieldsWithoutUpperCaseEmail.getPassword());
        assertFalse(fieldsWithoutUpperCaseEmail.getPassword()
                                               .matches(String.valueOf(passwordPattern)));

        System.out.println("password should not contain spaces : " + fieldsWithSpacesEmail.getPassword());
        assertFalse(fieldsWithSpacesEmail.getPassword()
                                         .matches(String.valueOf(passwordPattern)));

        System.out.println("password should contain at least one LowerCase character : " + fieldsWithoutLowerCaseEmail.getPassword());
        assertFalse(fieldsWithoutLowerCaseEmail.getPassword()
                                               .matches(String.valueOf(passwordPattern)));
    }
}