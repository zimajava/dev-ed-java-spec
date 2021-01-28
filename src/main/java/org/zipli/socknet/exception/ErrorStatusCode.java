package org.zipli.socknet.exception;

public enum ErrorStatusCode {
    USER_ID_NULL("User's id is null", 11),
    USER_ID_DOES_NOT_CORRECT("User's id isn't correct", 12),
    DATA_IS_NULL("Data is null", 13),
    EMAIL_DOES_NOT_CORRECT("Email isn't correct", 14),
    USER_DOES_NOT_EXIST("User doesn't exist", 15),
    USER_DOES_NOT_PASS_EMAIL_CONFIRM("User doesn't pass email confirmation", 16),
    EMAIL_ALREADY_EXISTS("Email is already token", 17),
    PASSWORD_INCORRECT("The password is incorrect", 18),
    TOKEN_INVALID_OR_BROKEN("Token is invalid or broken", 19),
    PASSWORD_IS_NULL("Password can't be null", 20),
    USERS_DOES_NOT_EXIST("User's is null", 21),
    CHAT_ACCESS_ERROR("Only the creator can execute", 22),
    MESSAGE_ACCESS_ERROR("Only the author can execute", 23),
    CHAT_ALREADY_EXISTS("Chat already exists", 24),
    CHAT_NOT_EXISTS("Chat doesn't exist", 25),
    MESSAGE_NOT_EXISTS("Message doesn't exist", 26),
    UNEXPECTED_EXCEPTION("Unexpected exception", 27),
    VIDEO_CALL_EXCEPTION("Video conferencing exception", 28),
    FILE_ACCESS_ERROR("Only the author can execute", 29),
    GRID_FS_FILE_IS_NOT_FOUND("Grid fs file is not found", 30),
    FILE_WAS_NOT_LOADING_CORRECT("File was not loading correct", 31),
    FILE_IS_NOT_IN_A_DB("The file is not in the database", 32),
    INCORRECT_REQUEST("Incorrect request", 33),
    ROOM_NOT_EXIT("Room doesn't exist", 34),
    ROOM_ALREADY_EXISTS("Room already exists", 35),
    PARAM_IS_NULL("Parameter by search is null", 36),
    USERS_DOES_NOT_EXIST_BY_PARAM("Users weren't found by these parameters", 37),
    PARAM_TOO_SHORT("Parameter is shorter than 3 characters", 38);

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
