package org.zipli.socknet.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.IOException;

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
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);

            Command command = Command.valueOf(node.findValue("command").asText());
            JsonNode data = node.findValue("data");
            switch (command) {
                case CHAT_DELETE:
                case CHAT_JOIN:
                case CHAT_LEAVE:
                case CHAT_UPDATE:
                case CHAT_GROUP_CREATE:
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
                default:
                    return new WsMessage(command, new BaseData(
                            data.findValue("idUser").asText(),
                            data.findValue("idChat").asText()
                    ));
            }
        }

    }

}
