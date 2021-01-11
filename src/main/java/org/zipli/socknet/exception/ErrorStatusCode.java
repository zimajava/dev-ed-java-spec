package org.zipli.socknet.exception;

public enum ErrorStatusCode {
    USER_ID_NULL(1),
    USER_ID_DOES_NOT_CORRECT(2),
    DATA_IS_NULL(3),
    EMAIL_DOES_NOT_CORRECT(4),
    USER_DOES_NOT_EXIST(1),
    USER_DOES_NOT_PASS_EMAIL_CONFIRM(2),
    EMAIL_ALREADY_EXISTS(3),
    TOKEN_INVALID_OR_BROKEN(1),
    USER_NOT_FOUND(1);

    private final int value;

    ErrorStatusCode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
