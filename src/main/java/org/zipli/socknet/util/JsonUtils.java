package org.zipli.socknet.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonUtils {
    public static ObjectMapper json;

    static {
        json = new ObjectMapper();
        json.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        json.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        json.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        json.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
    }

    public static String jsonWriteHandle(Object object) {
        String result = "{}";
        try {
            result = json.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException was thrown at " + object, e.getMessage());
        }
        return result;
    }
}
