package org.zipli.socknet.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.oauth2.sdk.ErrorObject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.zipli.socknet.repository.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class JsonUtilsTest {

    @Test
    public void jsonWriteHandle_Pass() {
        String expected = "{\"email\":\"i@mail.com\",\"password\":\"password\",\"userName\":\"username\",\"nickName\":\"nickname\"}";
        User user = new User("i@mail.com", "password", "username", "nickname");

        String actual = JsonUtils.jsonWriteHandle(user);

        assertEquals(expected, actual);
    }

    @Test
    public void jsonWriteHandle_Fail() throws JsonProcessingException {
        String expected = "{}";
        ObjectMapper om = Mockito.spy(new ObjectMapper());
        Mockito.when(om.writeValueAsString(ErrorObject.class)).thenThrow(new JsonProcessingException("") {
        });

        assertEquals(expected, JsonUtils.jsonWriteHandle(om));
    }
}
