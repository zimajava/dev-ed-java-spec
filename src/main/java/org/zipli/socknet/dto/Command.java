package org.zipli.socknet.dto;

public enum Command {
    CHAT_CREATE,
    CHAT_UPDATE,
    CHAT_DELETE,
    CHAT_LEAVE,
    CHAT_USER_ADD,
    CHATS_GET_BY_USER_ID,
    MESSAGE_SEND,
    MESSAGE_READ,
    MESSAGE_UPDATE,
    MESSAGE_DELETE,
    MESSAGES_GET_BY_CHAT_ID,
    ERROR_CREATE_CONNECT,
    FILE_SEND,
    FILE_DELETE,
    VIDEO_CALL_START,
    VIDEO_CALL_JOIN,
    VIDEO_CALL_EXIT
}
