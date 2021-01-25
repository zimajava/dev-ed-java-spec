package org.zipli.socknet.exception;

public enum ErrorStatusCode {
    USER_ID_NULL("User's id is null", 101),
    USER_ID_DOES_NOT_CORRECT("User's id isn't correct", 102),
    DATA_IS_NULL("Data is null", 103),
    EMAIL_DOES_NOT_CORRECT("Email isn't correct", 104),
    USER_DOES_NOT_EXIST("User doesn't exist", 105),
    USER_DOES_NOT_PASS_EMAIL_CONFIRM("User doesn't pass email confirmation", 106),
    EMAIL_ALREADY_EXISTS("Email is already token", 107),
    PASSWORD_INCORRECT("The password is incorrect", 108),
    TOKEN_INVALID_OR_BROKEN("Token is invalid or broken", 109),
    PASSWORD_IS_NULL("Password can't be null", 110),
    USERS_DOES_NOT_EXIST("User's is null", 111),
    CHAT_ACCESS_ERROR("Only the creator can execute", 112),
    MESSAGE_ACCESS_ERROR("Only the author can execute", 113),
    CHAT_ALREADY_EXISTS("Chat already exists", 114),
    CHAT_NOT_EXISTS("Chat doesn't exist", 115),
    MESSAGE_NOT_EXISTS("Message doesn't exist", 116),
    UNEXPECTED_EXCEPTION("Unexpected exception", 117),
    VIDEO_CALL_EXCEPTION("Video conferencing exception", 118),
    FILE_ACCESS_ERROR("Only the author can execute", 119),
    GRID_FS_FILE_IS_NOT_FOUND("Grid fs file is not found", 120),
    FILE_WAS_NOT_LOADING_CORRECT("File was not loading correct", 121),
    FILE_IS_NOT_IN_A_DB("The file is not in the database", 122),
    INCORRECT_REQUEST("Incorrect request", 123),
    ROOM_NOT_EXIT("Room doesn't exist", 124),
    ROOM_ALREADY_EXISTS("Room already exists", 125),
    PARAM_IS_NULL("Parameter by search is null", 126),
    USERS_DOES_NOT_EXIST_BY_PARAM("Users weren't found by these parameters", 127),
    PARAM_TOO_SHORT("Parameter is shorter than 3 characters", 128);

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
