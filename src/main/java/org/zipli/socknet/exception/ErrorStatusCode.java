package org.zipli.socknet.exception;

public enum ErrorStatusCode {
    USER_ID_NULL("User's id is null", 1),
    USER_ID_DOES_NOT_CORRECT("User's id isn't correct", 2),
    DATA_IS_NULL("Data is null", 3),
    EMAIL_DOES_NOT_CORRECT("Email isn't correct", 4),
    USER_DOES_NOT_EXIST("User doesn't exist", 5),
    USER_DOES_NOT_PASS_EMAIL_CONFIRM("User doesn't pass email confirmation", 6),
    EMAIL_ALREADY_EXISTS("Email is already taken", 7),
    PASSWORD_INCORRECT("The password is incorrect", 8),
    TOKEN_INVALID_OR_BROKEN("Token is invalid or broken", 9),
    PASSWORD_IS_NULL("Password can't be null", 10),
    USERS_DOES_NOT_EXIST("User's is null", 11),
    PARAM_IS_NULL("Parameter by search is null", 12),
    USERS_DOES_NOT_EXIST_BY_PARAM("Users weren't found by these parameters", 13),
    PARAM_TOO_SHORT("Parameter is shorter than 3 characters", 13);

    private final int value;
    private final String message;

    ErrorStatusCode(String message, int value) {
        this.message = message;
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }

}
