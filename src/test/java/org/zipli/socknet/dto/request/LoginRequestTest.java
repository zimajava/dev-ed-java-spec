package org.zipli.socknet.dto.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LoginRequestTest {
    private final Pattern emailPattern = Pattern.compile("^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*\\.\\w{2,}$");
    private final Pattern passwordPattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,16}$");
    private final Pattern userNamePattern = Pattern.compile("^[a-zA-Z_-]{2,16}$");

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
    private LoginRequest nullParameters;

    @BeforeEach
    public void init() {
        validParametersEmail = new LoginRequest(
                "newUser2@gmail.com",
                "ugyur2Wa4");

        validParametersUserName = new LoginRequest(
                "uyfrjjjJk",
                "ugyur2Wa4");

        emptyLoginRequestEmail = new LoginRequest("", "");

        emptyLoginRequestUserName = new LoginRequest("", "");

        nullParameters = new LoginRequest(null, null);

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
                "kjfdkdfkl",
                "ugyur2wa4");

        fieldsWithoutLowerCaseEmail = new LoginRequest(
                "NEWUSER2@GMAIL.COM",
                "UGHSK2WGKK4");

        fieldsWithoutLowerCaseUserName = new LoginRequest(
                "UGHSKWGKK",
                "UGHSK2WGKK4");

        fieldsWithSpecificCharsEmail = new LoginRequest(
                "n_ew.j-ser2@gmail.com",
                "ugyur2Wa4");

        fieldsWithSpecificCharsUserName = new LoginRequest(
                "uyfrj_jj-Jk",
                "ugyur2Wa4");

        fieldsWithTwoDogs = new LoginRequest(
                "new@User2@gmail.com",
                "ugyur2Wa4");
    }

    @Test
    public void null_ShouldThrowsNullPointer() {
        assertThrows(NullPointerException.class, () -> nullParameters.getLogin()
                .matches(String.valueOf(emailPattern)));
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
        assertTrue(validParametersEmail.getLogin()
                .matches(String.valueOf(emailPattern)));

        assertTrue(fieldsWithoutUpperCaseEmail.getLogin()
                .matches(String.valueOf(emailPattern)));

        assertTrue(fieldsWithSpecificCharsEmail.getLogin()
                .matches(String.valueOf(emailPattern)));

        assertTrue(validParametersUserName.getLogin()
                .matches(String.valueOf(userNamePattern)));

        assertTrue(fieldsWithoutLowerCaseUserName.getLogin()
                .matches(String.valueOf(userNamePattern)));

        assertTrue(fieldsWithoutUpperCaseUserName.getLogin()
                .matches(String.valueOf(userNamePattern)));

        assertTrue(fieldsWithSpecificCharsUserName.getLogin()
                .matches(String.valueOf(userNamePattern)));
    }

    @Test
    public void login_ShouldNotPassValidation() {
        assertFalse(fieldsWithTwoDogs.getLogin()
                .matches(String.valueOf(emailPattern)));

        assertFalse(fieldsWithSpacesEmail.getLogin()
                .matches(String.valueOf(emailPattern)));

        assertFalse(fieldsLessThan8Email.getLogin()
                .matches(String.valueOf(emailPattern)));

        assertFalse(fieldsLessThan8UserName.getLogin()
                .matches(String.valueOf(userNamePattern)));

        assertFalse(fieldsMoreThan16UserName.getLogin()
                .matches(String.valueOf(userNamePattern)));

        assertFalse(fieldsWithSpacesUserName.getLogin()
                .matches(String.valueOf(userNamePattern)));
    }

    @Test
    public void password_ShouldPassValidation() {
        assertTrue(validParametersEmail.getPassword()
                .matches(String.valueOf(passwordPattern)));

        assertTrue(validParametersUserName.getPassword()
                .matches(String.valueOf(passwordPattern)));
    }

    @Test
    public void password_ShouldNotPassValidation() {
        assertFalse(fieldsLessThan8Email.getPassword()
                .matches(String.valueOf(passwordPattern)));

        assertFalse(fieldsMoreThan16Email.getPassword()
                .matches(String.valueOf(passwordPattern)));

        assertFalse(fieldsWithoutUpperCaseEmail.getPassword()
                .matches(String.valueOf(passwordPattern)));

        assertFalse(fieldsWithSpacesEmail.getPassword()
                .matches(String.valueOf(passwordPattern)));

        assertFalse(fieldsWithoutLowerCaseEmail.getPassword()
                .matches(String.valueOf(passwordPattern)));
    }
}
