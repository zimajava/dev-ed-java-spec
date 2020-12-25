package org.zipli.socknet.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {
    private static final ObjectMapper json = new ObjectMapper();

    public static String jsonWriteHandle(Object object) {
        String result = "{}";
        try {
            result = json.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return result;
        }
        return result;
    }
}
