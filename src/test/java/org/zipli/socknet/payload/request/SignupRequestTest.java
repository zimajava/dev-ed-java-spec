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
    private final Pattern userNamePattern = Pattern.compile("^[a-zA-Z0-9_-]{8,16}$");
    private final Pattern nickNamePattern = Pattern.compile("^[a-zA-Z0-9_ .-]{8,16}$");

    private SignupRequest validParameters;
    private SignupRequest fieldsLessThan8;
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
                "uyfrjjj3Jk8",
                "gfr5367HP");

        fieldsWithSpecificChars = new SignupRequest(
                "n_ew.j-ser2@gmail.com",
                "ugyur2Wa4",
                "uyfrj_jj3-Jk8",
                "gf.r5_367-H P");

        fieldsWithTwoDogs = new SignupRequest(
                "new@User2@gmail.com",
                "ugyur2Wa4",
                "uyfrjjj3Jk8",
                "gfr5367HP");

        emptySignupRequest = new SignupRequest("", "", "", "");

        fieldsLessThan8 = new SignupRequest(
                "news2W@gmail.c",
                "ugyur3H",
                "uyfrjjj",
                "gfr5367");

        fieldsMoreThan16 = new SignupRequest(
                "ewUnjnoe899249mlfser2@gmail.com",
                "ugyur2Wa4sdjkfjkskllsllj",
                "uyfrjjj3Jk8sfkdkjjgdjgdl",
                "gfr5367HPdksdfjkdjkfjkdk");

        fieldsWithoutUpperCase = new SignupRequest(
                "newuser2@gmail.com",
                "ugyur2wa4",
                "kjfdk87dfkl",
                "gfr5367hp");

        fieldsWithoutLowerCase = new SignupRequest(
                ":NEWUSER2@GMAIL.COM",
                "UGHSK2WGKK4",
                "UGHSK2WGKK4",
                "LKNFFEK2WGKK4");

        fieldsWithSpaces = new SignupRequest(
                "new User2@gmail.com",
                "ugyur 2Wa4",
                "uyfrj jj3Jk8",
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
        System.out.println("email is valid : " + validParameters.getEmail());
        assertTrue(validParameters.getEmail()
                                  .matches(String.valueOf(emailPattern)));

        System.out.println("email is valid : " + fieldsWithoutUpperCase.getEmail());
        assertTrue(fieldsWithoutUpperCase.getEmail()
                                  .matches(String.valueOf(emailPattern)));

        System.out.println("email is valid : " + fieldsWithSpecificChars.getEmail());
        assertTrue(fieldsWithSpecificChars.getEmail()
                                         .matches(String.valueOf(emailPattern)));
    }

    @Test
    public void email_ShouldNotPassValidation() {
        System.out.println("email contains two @ characters : " + fieldsWithTwoDogs.getEmail());
        assertFalse(fieldsWithTwoDogs.getEmail()
                                    .matches(String.valueOf(emailPattern)));

        System.out.println("email should not contain spaces : " + fieldsWithSpaces.getEmail());
        assertFalse(fieldsWithSpaces.getEmail()
                                    .matches(String.valueOf(emailPattern)));

        System.out.println("email should contain at least one LowerCase character : " + fieldsWithoutLowerCase.getEmail());
        assertFalse(fieldsWithoutLowerCase.getEmail()
                                          .matches(String.valueOf(emailPattern)));

        System.out.println("email should contain at least two characters after . : " + fieldsLessThan8.getEmail());
        assertFalse(fieldsLessThan8.getEmail()
                                    .matches(String.valueOf(emailPattern)));
    }

    @Test
    public void password_ShouldPassValidation() {
        System.out.println("password is valid : " + validParameters.getPassword());
        assertTrue(validParameters.getPassword()
                                  .matches(String.valueOf(passwordPattern)));
    }

    @Test
    public void password_ShouldNotPassValidation() {
        System.out.println("password size is less than 8 characters : " + fieldsLessThan8.getPassword());
        assertFalse(fieldsLessThan8.getPassword()
                                   .matches(String.valueOf(passwordPattern)));

        System.out.println("password size is more than 16 characters : " + fieldsMoreThan16.getPassword());
        assertFalse(fieldsMoreThan16.getPassword()
                                    .matches(String.valueOf(passwordPattern)));

        System.out.println("password should contain at least one UpperCase character : " + fieldsWithoutUpperCase.getPassword());
        assertFalse(fieldsWithoutUpperCase.getPassword()
                                          .matches(String.valueOf(passwordPattern)));

        System.out.println("password should not contain spaces : " + fieldsWithSpaces.getPassword());
        assertFalse(fieldsWithSpaces.getPassword()
                                    .matches(String.valueOf(passwordPattern)));

        System.out.println("password should contain at least one LowerCase character : " + fieldsWithoutLowerCase.getPassword());
        assertFalse(fieldsWithoutLowerCase.getPassword()
                                          .matches(String.valueOf(passwordPattern)));
    }

    @Test
    public void userName_ShouldPassValidation() {
        System.out.println("userName is valid : " + validParameters.getUserName());
        assertTrue(validParameters.getUserName()
                                  .matches(String.valueOf(userNamePattern)));

        System.out.println("userName is valid : " + fieldsWithoutLowerCase.getUserName());
        assertTrue(fieldsWithoutLowerCase.getUserName()
                                         .matches(String.valueOf(userNamePattern)));

        System.out.println("userName is valid : " + fieldsWithoutUpperCase.getUserName());
        assertTrue(fieldsWithoutUpperCase.getUserName()
                                         .matches(String.valueOf(userNamePattern)));

        System.out.println("userName is valid : " + fieldsWithSpecificChars.getUserName());
        assertTrue(fieldsWithSpecificChars.getUserName()
                                         .matches(String.valueOf(userNamePattern)));
    }

    @Test
    public void userName_ShouldNotPassValidation() {
        System.out.println("userName size is less than 8 characters : " + fieldsLessThan8.getUserName());
        assertFalse(fieldsLessThan8.getUserName()
                                   .matches(String.valueOf(userNamePattern)));

        System.out.println("userName size is more than 16 characters : " + fieldsMoreThan16.getUserName());
        assertFalse(fieldsMoreThan16.getUserName()
                                    .matches(String.valueOf(userNamePattern)));

        System.out.println("userName should not contain spaces : " + fieldsWithSpaces.getUserName());
        assertFalse(fieldsWithSpaces.getUserName()
                                    .matches(String.valueOf(userNamePattern)));
    }

    @Test
    public void nickName_ShouldPassValidation() {
        System.out.println("nickName is valid : " + validParameters.getNickName());
        assertTrue(validParameters.getNickName()
                                  .matches(String.valueOf(nickNamePattern)));

        System.out.println("nickName is valid : " + fieldsWithoutLowerCase.getNickName());
        assertTrue(fieldsWithoutLowerCase.getNickName()
                                         .matches(String.valueOf(nickNamePattern)));

        System.out.println("nickName is valid : " + fieldsWithoutUpperCase.getNickName());
        assertTrue(fieldsWithoutUpperCase.getNickName()
                                         .matches(String.valueOf(nickNamePattern)));

        System.out.println("nickName is valid : " + fieldsWithSpecificChars.getNickName());
        assertTrue(fieldsWithSpecificChars.getNickName()
                                         .matches(String.valueOf(nickNamePattern)));
    }

    @Test
    public void nickName_ShouldNotPassValidation() {
        System.out.println("nickName size is less than 8 characters : " + fieldsLessThan8.getNickName());
        assertFalse(fieldsLessThan8.getNickName()
                                   .matches(String.valueOf(nickNamePattern)));

        System.out.println("nickName size is more than 16 characters : " + fieldsMoreThan16.getNickName());
        assertFalse(fieldsMoreThan16.getNickName()
                                    .matches(String.valueOf(nickNamePattern)));
    }
}