package org.zipli.socknet.payload.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class SignupRequestTest {
    private final Pattern emailPattern = Pattern.compile("^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*\\.\\w{2,}$");
    private final Pattern passwordPattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,16}$");
    private final Pattern userNamePattern = Pattern.compile("^[a-zA-Z_-]{2,16}$");
    private final Pattern nickNamePattern = Pattern.compile("^.{1,16}$");

    private SignupRequest validParameters;
    private SignupRequest fieldsLessThan8;
    private SignupRequest fieldsIsEmpty;
    private SignupRequest fieldsMoreThan16;
    private SignupRequest fieldsWithoutUpperCase;
    private SignupRequest fieldsWithoutLowerCase;
    private SignupRequest fieldsWithSpaces;
    private SignupRequest emptySignupRequest;
    private SignupRequest fieldsWithSpecificChars;
    private SignupRequest fieldsWithTwoDogs;

    @BeforeEach
    public void init() {
        validParameters = new SignupRequest(
                "newUser2@gmail.com",
                "ugyur2Wa4",
                "uZ-y",
                "gfr5367HP");

        fieldsWithSpecificChars = new SignupRequest(
                "n_ew.j-ser2@gmail.com",
                "ugyur2Wa4",
                "uyfrj_jjv-Jkn",
                "gf.r5_367-H P");

        fieldsWithTwoDogs = new SignupRequest(
                "new@User2@gmail.com",
                "ugyur2Wa4",
                "uyfrjjjxJkz",
                "gfr5367HP");

        emptySignupRequest = new SignupRequest("", "", "", "");

        fieldsLessThan8 = new SignupRequest(
                "news2W@gmail.c",
                "ugyur3H",
                "uyfrjjj",
                "gfr5367");

        fieldsIsEmpty = new SignupRequest(
                "",
                "",
                "",
                "");

        fieldsMoreThan16 = new SignupRequest(
                "ewUnjnoe899249mlfser2@gmail.com",
                "ugyur2Wa4sdjkfjkskllsllj",
                "uyfrjjjqJkrsfkdkjjgdjgdl",
                "gfr5367HPdksdfjkdjkfjkdk");

        fieldsWithoutUpperCase = new SignupRequest(
                "newuser2@gmail.com",
                "ugyur2wa4",
                "kjfdkqwdfkl",
                "gfr5367hp");

        fieldsWithoutLowerCase = new SignupRequest(
                ":NEWUSER2@GMAIL.COM",
                "UGHSK2WGKK4",
                "UGHSKRWGKKE",
                "LKNFFEK2WGKK4");

        fieldsWithSpaces = new SignupRequest(
                "new User2@gmail.com",
                "ugyur 2Wa4",
                "uyfrj jjeJkb",
                "gfr53 67HP");
    }

    @Test
    public void isBlank_ShouldReturnFalse() {
        assertFalse(validParameters.getEmail()
                .isBlank());
        assertFalse(validParameters.getPassword()
                .isBlank());
        assertFalse(validParameters.getUserName()
                .isBlank());
        assertFalse(validParameters.getNickName()
                .isBlank());
    }

    @Test
    public void isBlank_ShouldReturnTrue() {
        assertTrue(emptySignupRequest.getEmail()
                .isBlank());
        assertTrue(emptySignupRequest.getPassword()
                .isBlank());
        assertTrue(emptySignupRequest.getUserName()
                .isBlank());
        assertTrue(emptySignupRequest.getNickName()
                .isBlank());
    }

    @Test
    public void email_ShouldPassValidation() {
        assertTrue(validParameters.getEmail()
                .matches(String.valueOf(emailPattern)));

        assertTrue(fieldsWithoutUpperCase.getEmail()
                .matches(String.valueOf(emailPattern)));

        assertTrue(fieldsWithSpecificChars.getEmail()
                .matches(String.valueOf(emailPattern)));
    }

    @Test
    public void email_ShouldNotPassValidation() {
        assertFalse(fieldsWithTwoDogs.getEmail()
                .matches(String.valueOf(emailPattern)));

        assertFalse(fieldsWithSpaces.getEmail()
                .matches(String.valueOf(emailPattern)));

        assertFalse(fieldsWithoutLowerCase.getEmail()
                .matches(String.valueOf(emailPattern)));

        assertFalse(fieldsLessThan8.getEmail()
                .matches(String.valueOf(emailPattern)));
    }

    @Test
    public void password_ShouldPassValidation() {
        assertTrue(validParameters.getPassword()
                .matches(String.valueOf(passwordPattern)));
    }

    @Test
    public void password_ShouldNotPassValidation() {
        assertFalse(fieldsLessThan8.getPassword()
                .matches(String.valueOf(passwordPattern)));

        assertFalse(fieldsMoreThan16.getPassword()
                .matches(String.valueOf(passwordPattern)));

        assertFalse(fieldsWithoutUpperCase.getPassword()
                .matches(String.valueOf(passwordPattern)));

        assertFalse(fieldsWithSpaces.getPassword()
                .matches(String.valueOf(passwordPattern)));

        assertFalse(fieldsWithoutLowerCase.getPassword()
                .matches(String.valueOf(passwordPattern)));
    }

    @Test
    public void userName_ShouldPassValidation() {
        assertTrue(validParameters.getUserName()
                .matches(String.valueOf(userNamePattern)));

        assertTrue(fieldsWithoutLowerCase.getUserName()
                .matches(String.valueOf(userNamePattern)));

        assertTrue(fieldsWithoutUpperCase.getUserName()
                .matches(String.valueOf(userNamePattern)));

        assertTrue(fieldsWithSpecificChars.getUserName()
                .matches(String.valueOf(userNamePattern)));
    }

    @Test
    public void userName_ShouldNotPassValidation() {
        assertFalse(fieldsIsEmpty.getUserName()
                .matches(String.valueOf(userNamePattern)));

        assertFalse(fieldsMoreThan16.getUserName()
                .matches(String.valueOf(userNamePattern)));

        assertFalse(fieldsWithSpaces.getUserName()
                .matches(String.valueOf(userNamePattern)));
    }

    @Test
    public void nickName_ShouldPassValidation() {
        assertTrue(validParameters.getNickName()
                .matches(String.valueOf(nickNamePattern)));

        assertTrue(fieldsWithoutLowerCase.getNickName()
                .matches(String.valueOf(nickNamePattern)));

        assertTrue(fieldsWithoutUpperCase.getNickName()
                .matches(String.valueOf(nickNamePattern)));

        assertTrue(fieldsWithSpecificChars.getNickName()
                .matches(String.valueOf(nickNamePattern)));
    }

    @Test
    public void nickName_ShouldNotPassValidation() {
        assertFalse(fieldsIsEmpty.getNickName()
                .matches(String.valueOf(nickNamePattern)));

        assertFalse(fieldsMoreThan16.getNickName()
                .matches(String.valueOf(nickNamePattern)));
    }

}