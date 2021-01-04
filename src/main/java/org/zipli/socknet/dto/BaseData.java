package org.zipli.socknet.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.IOException;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize(using = BaseData.Deserializer.class)
public class BaseData {

    private String idUser;
    private String idChat;

    public BaseData(String idUser) {
        this.idUser = idUser;
    }

    public class Deserializer extends JsonDeserializer<BaseData> {

        @Override
        public BaseData deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            ObjectCodec codec = jsonParser.getCodec();
            JsonNode node = codec.readTree(jsonParser);

            Command command = Command.valueOf(node.findValue("Command").asText());
            switch (command) {
                case CHAT_DELETE:
                    return new ChatData();
            }

//            BigDecimal amount = new BigDecimal(node.get(1).asText());
//
//            return new OkexPublicTrade(price, amount, count);
       return new BaseData();
        }

    }
}

