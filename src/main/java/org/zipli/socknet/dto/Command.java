package org.zipli.socknet.dto;

public enum Command {
    CHAT_GROUP_CREATE,
    CHAT_PRIVATE_CREATE,
    CHAT_UPDATE,
    CHAT_DELETE,
    CHAT_LEAVE,
    CHAT_JOIN,
    CHATS_GET_BY_USER_ID,
    MESSAGE_SEND,
    MESSAGE_READ,
    MESSAGE_UPDATE,
    MESSAGE_DELETE,
    MESSAGES_GET_BY_CHAT_ID,
    ERROR_CREATE_CONNECT
}
