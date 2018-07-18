package com.tddapps.actions;

import com.fasterxml.jackson.databind.JsonNode;
import com.tddapps.actions.response.TextMessage;
import com.tddapps.controllers.ActionBodyParseException;
import com.tddapps.controllers.ActionProcessException;
import com.tddapps.controllers.HttpJsonResponse;
import com.tddapps.utils.JsonNodeHelper;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class HeartBeatPostActionTest {
    private final HeartBeatPostAction action = new HeartBeatPostAction();
    private final String MAXIMUM_LENGTH_ALLOWED_STRING = StringUtils.leftPad("", 100, "0");

    @Test
    public void ReadsTheHostId(){
        HeartBeatPostActionInput input = parse("{\"hostId\": \"superHost1\"}");

        assertEquals("superHost1", input.getHostId());
        assertEquals(HeartBeatPostActionInput.DEFAULT_INTERVAL_MS, input.getIntervalMs());
    }

    @Test
    public void ReadsTheIntervalMs(){
        HeartBeatPostActionInput input = parse("{\"hostId\": \"superHost1\", \"intervalMs\": 3000}");

        assertEquals("superHost1", input.getHostId());
        assertEquals(3000, input.getIntervalMs());
    }

    @Test
    public void ReadsTheIntervalMsWhenTheValueIsAFloat(){
        HeartBeatPostActionInput input = parse("{\"hostId\": \"superHost1\", \"intervalMs\": 3000.45}");

        assertEquals("superHost1", input.getHostId());
        assertEquals(3000, input.getIntervalMs());
    }

    @Test
    public void ReadsTheIntervalMsWhenTheProvidedValueIsAString(){
        HeartBeatPostActionInput input = parse("{\"hostId\": \"superHost1\", \"intervalMs\": \"3000\"}");

        assertEquals("superHost1", input.getHostId());
        assertEquals(3000, input.getIntervalMs());
    }

    @Test
    public void ReadsTheMaximumLengthHostId(){
        HeartBeatPostActionInput input = parse(String.format(
                "{\"hostId\": \"%s\"}", MAXIMUM_LENGTH_ALLOWED_STRING
        ));
        assertEquals(MAXIMUM_LENGTH_ALLOWED_STRING, input.getHostId());
    }

    @Test
    public void ParsingFailsWhenHostIdIsMissing(){
        parseShouldThrow("{}");
        parseShouldThrow("{\"hostId\": \"\"}");
        parseShouldThrow("{\"hostId\": \"   \"}");
    }

    @Test
    public void ParsingFailsWhenHostIdIsNotAlphanumeric(){
        parseShouldThrow("{\"hostId\": \"-!@#$$%^%^ &^&\"}");
    }

    @Test
    public void ParsingFailsWhenHostIdIsTooLong(){
        parseShouldThrow(String.format(
                "{\"hostId\": \"X%s\"}", MAXIMUM_LENGTH_ALLOWED_STRING
        ));
    }
    
    @Test
    public void ParsingAssumesDefaultWhenIntervalMsIsNotNumeric(){
        assertEquals(HeartBeatPostActionInput.DEFAULT_INTERVAL_MS, parse("{\"hostId\": \"superHost1\", \"intervalMs\": null}").getIntervalMs());
        assertEquals(HeartBeatPostActionInput.DEFAULT_INTERVAL_MS, parse("{\"hostId\": \"superHost1\", \"intervalMs\": \"\"}").getIntervalMs());
        assertEquals(HeartBeatPostActionInput.DEFAULT_INTERVAL_MS, parse("{\"hostId\": \"superHost1\", \"intervalMs\": \" \"}").getIntervalMs());
        assertEquals(HeartBeatPostActionInput.DEFAULT_INTERVAL_MS, parse("{\"hostId\": \"superHost1\", \"intervalMs\": \"pete\"}").getIntervalMs());
    }

    @Test
    public void ParsingFailsWhenIntervalMsIsOutOfBoundaries(){
        parseShouldThrow("{\"hostId\": \"host1\", \"intervalMs\": 999}");
        parseShouldThrow("{\"hostId\": \"host1\", \"intervalMs\": \"999\"}");
        parseShouldThrow("{\"hostId\": \"host1\", \"intervalMs\": 43200001}");
        parseShouldThrow("{\"hostId\": \"host1\", \"intervalMs\": \"43200001\"}");
    }

    @Test
    public void ProcessReturnsSuccess(){
        HttpJsonResponse<TextMessage> result = process("host1");

        assertEquals(HttpJsonResponse.Success(TextMessage.OK), result);
    }

    private void parseShouldThrow(String body){
        try {
            parseInternal(body);
            fail("Expected Exception to have been thrown");
        } catch (ActionBodyParseException e) {
            assertNotNull(e);
        }
    }

    private HeartBeatPostActionInput parse(String body){
        try {
            return parseInternal(body);
        } catch (ActionBodyParseException e) {
            fail("Parse should not have thrown", e);
            return null;
        }
    }

    private HeartBeatPostActionInput parseInternal(String body) throws ActionBodyParseException {
        JsonNode seededBody = null;
        try {
            seededBody = JsonNodeHelper.parse(body);
        } catch (IOException e) {
            fail("Parsing seeded body shouldn't throw", e);
        }
        return action.parse(seededBody);
    }

    private HttpJsonResponse<TextMessage> process(String hostId){
        try {
            return action.process(new HeartBeatPostActionInput(hostId, HeartBeatPostActionInput.DEFAULT_INTERVAL_MS));
        } catch (ActionProcessException e) {
            fail("Process should not have thrown", e);
            return null;
        }
    }
}
