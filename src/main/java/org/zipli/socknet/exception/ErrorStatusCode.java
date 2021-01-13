package org.zipli.socknet.exception;

public enum ErrorStatusCode {
    USER_ID_NULL("User's id is null", 1),
    USER_ID_DOES_NOT_CORRECT("User's id isn't correct", 2),
    DATA_IS_NULL("Data is null", 3),
    EMAIL_DOES_NOT_CORRECT("Email isn't correct", 4),
    USER_DOES_NOT_EXIST("User doesn't exist", 1),
    USER_DOES_NOT_PASS_EMAIL_CONFIRM("User doesn't pass email confirmation", 2),
    EMAIL_ALREADY_EXISTS("Email is already taken", 3),
    TOKEN_INVALID_OR_BROKEN("Token is invalid or broken", 1),
    PASSWORD_IS_NULL("Password can't be null", 2),
    FORBIDDEN_DELETE_MESSAGE("Can't delete message emitter", 1);

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
