package org.zipli.socknet.dto;

public enum Command {
    CREATE_GROUP_CHAT,
    CREATE_PRIVATE_CHAT,
    UPDATE_CHAT,
    REMOVE_CHAT,
    LEAVE_CHAT,
    JOIN_CHAT,
    GET_MESSAGE,
    SEND_MESSAGE,
    DELETE_MESSAGE,
    SHOW_CHATS_BY_USER,
    ERROR_CREATE_CONNECT
}
