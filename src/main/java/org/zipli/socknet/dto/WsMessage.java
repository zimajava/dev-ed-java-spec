package org.zipli.socknet.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.zipli.socknet.dto.video.VideoData;
import org.zipli.socknet.util.JsonUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize(using = WsMessage.Deserializer.class)
public class WsMessage {
    private Command command;
    private BaseData data;

    public static class Deserializer extends JsonDeserializer<WsMessage> {

        @Override
        public WsMessage deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            JsonNode node = JsonUtils.json.readTree(jsonParser);
            Command command = Command.valueOf(node.findValue("command").asText());
            JsonNode data = node.findValue("data");
            switch (command) {
                case CHAT_GROUP_CREATE:
                    List<String> users = new ArrayList<>();
                    data.get("groupUsersIds").forEach(x -> users.add(x.asText()));
                    return new WsMessage(command, new ChatGroupData(
                            data.findValue("idUser").asText(),
                            data.findValue("idChat").asText(),
                            data.findValue("chatName").asText(),
                            users));
                case CHAT_DELETE:
                case CHAT_JOIN:
                case CHAT_LEAVE:
                case CHAT_UPDATE:
                case CHAT_PRIVATE_CREATE:
                case CHATS_GET_BY_USER_ID:
                    return new WsMessage(command, new ChatData(
                            data.findValue("idUser").asText(),
                            data.findValue("idChat").asText(),
                            data.findValue("chatName").asText(),
                            data.findValue("secondUserId").asText()
                    ));
                case MESSAGE_DELETE:
                case MESSAGE_READ:
                case MESSAGE_SEND:
                case MESSAGE_UPDATE:
                case MESSAGES_GET_BY_CHAT_ID:
                    return new WsMessage(command, new MessageData(
                            data.findValue("idUser").asText(),
                            data.findValue("idChat").asText(),
                            data.findValue("messageId").asText(),
                            data.findValue("textMessage").asText()
                    ));
                case VIDEO_CALL_START:
                case VIDEO_CALL_JOIN:
                    return new WsMessage(command, new VideoData(
                            data.findValue("idUser").asText(),
                            data.findValue("idChat").asText(),
                            data.findValue("userName").asText(),
                            data.findValue("chatName").asText(),
                            data.findValue("signal").asText()
                    ));
                case FILE_SEND:
                case FILE_DELETE:
                    return new WsMessage(command, new FileData(
                            data.findValue("idUser").asText(),
                            data.findValue("idChat").asText(),
                            data.findValue("fileId").asText(),
                            data.findValue("bytes").asText()
                    ));
                default:
                    return new WsMessage(command, new BaseData(
                            data.findValue("idUser").asText(),
                            data.findValue("idChat").asText()
                    ));
            }
        }

    }

}
