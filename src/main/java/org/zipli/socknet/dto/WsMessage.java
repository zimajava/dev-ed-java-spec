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
import java.util.Date;
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
                case CHAT_CREATE:
                    List<String> users = new ArrayList<>();
                    data.get("chatParticipants").forEach(x -> users.add(x.asText()));
                    return new WsMessage(command, new FullChatData(
                            data.findValue("userId").asText(),
                            data.findValue("chatName").asText(),
                            users,
                            data.findValue("isPrivate").asBoolean()
                    ));
                case CHAT_UPDATE:
                    return new WsMessage(command, new FullChatData(
                            data.findValue("userId").asText(),
                            data.findValue("chatId").asText(),
                            data.findValue("chatName").asText()
                    ));
                case CHATS_GET_BY_USER_ID:
                    return new WsMessage(command, new BaseData(
                            data.findValue("userId").asText()
                    ));
                case MESSAGE_SEND:
                    return new WsMessage(command, new MessageData(
                            data.findValue("userId").asText(),
                            data.findValue("chatId").asText(),
                            data.findValue("textMessage").asText(),
                            new Date(data.findValue("timestamp").asLong())
                    ));
                case MESSAGE_DELETE:
                case MESSAGE_READ:
                    return new WsMessage(command, new MessageData(
                            data.findValue("userId").asText(),
                            data.findValue("chatId").asText(),
                            data.findValue("messageId").asText()
                    ));
                case MESSAGE_UPDATE:
                    return new WsMessage(command, new MessageData(
                            data.findValue("userId").asText(),
                            data.findValue("chatId").asText(),
                            data.findValue("messageId").asText(),
                            data.findValue("textMessage").asText()
                    ));
                case VIDEO_CALL_START:
                case VIDEO_CALL_JOIN:
                    return new WsMessage(command, new VideoData(
                            data.findValue("userId").asText(),
                            data.findValue("chatId").asText(),
                            data.findValue("userName").asText(),
                            data.findValue("chatName").asText(),
                            data.findValue("signal").asText()
                    ));
                case FILE_SEND:
                case FILE_DELETE:
                    JsonNode bytesNode = data.get("bytes");
                    final int[] i = {0};
                    byte[] finalBytes = new byte[bytesNode.size()];
                    bytesNode.forEach(b -> finalBytes[i[0]++] = (byte) b.intValue());

                    return new WsMessage(command, new FileData(
                            data.findValue("userId").asText(),
                            data.findValue("chatId").asText(),
                            data.findValue("fileId").asText(),
                            data.findValue("title").asText(),
                            finalBytes
                    ));
                case CHAT_DELETE:
                case CHAT_USER_ADD:
                case CHAT_LEAVE:
                case MESSAGES_GET_BY_CHAT_ID:
                case VIDEO_CALL_EXIT:
                default:
                    return new WsMessage(command, new ChatData(
                            data.findValue("userId").asText(),
                            data.findValue("chatId").asText()));
            }
        }
    }
}
