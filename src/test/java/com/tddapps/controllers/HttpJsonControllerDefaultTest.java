package com.tddapps.controllers;

import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;


public class HttpJsonControllerDefaultTest {
    private final HttpJsonActionStub actionStub = new HttpJsonActionStub();
    private final HttpJsonControllerDefault<String, String> controller = new HttpJsonControllerDefault<>(actionStub);

    @Test
    public void ReturnsBadRequestWhenBodyMissing(){
        val input = new HashMap<String, Object>(){{
            put("key1", "value1");
        }};

        assertEquals(HttpJsonResponse.BadRequestWithMessage("Empty Request Body"), controller.process(input));
    }

    @Test
    public void ReturnsBadRequestWhenBodyIsInvalid(){
        assertEquals(
                HttpJsonResponse.BadRequestWithMessage("Empty Request Body"),
                processBody(null)
        );

        assertEquals(
                HttpJsonResponse.BadRequestWithMessage("Empty Request Body"),
                processBody("")
        );

        assertEquals(
                HttpJsonResponse.BadRequestWithMessage("Empty Request Body"),
                processBody("   ")
        );
    }

    @Test
    public void ReturnsBadRequestWhenBodyIsAnInvalidJson(){
        assertEquals(
                HttpJsonResponse.BadRequestWithMessage("Invalid json in request body"),
                processBody("this is incorrect")
        );
    }

    @Test
    public void ReturnsBadRequestWhenBodyIsNotTheExpectedJson(){
        actionStub.setSeededParseException(new ActionBodyParseException("userId expected"));

        assertEquals(
                HttpJsonResponse.BadRequestWithMessage("userId expected"),
                processBody("{\"name\": \"jsmith\"}")
        );
    }

    @Test
    public void ReturnsServerErrorWhenProcessingFails(){
        actionStub.setSeededProcessException(new ActionProcessException("database is down"));

        assertEquals(
                HttpJsonResponse.ServerErrorWithMessage("database is down"),
                processBody("{\"name\": \"jsmith\"}")
        );
    }

    @Test
    public void ReturnsTheProcessedParsedBody(){
        actionStub.setSeededParsedBody("parsed body");
        actionStub.setSeededResultBody("result body");
        actionStub.setSeededStatusCode(200);

        assertEquals(
                HttpJsonResponse.Success("result body"),
                processBody("{\"userId\": \"jdoe\"}")
        );
    }

    private HttpJsonResponse processBody(String body){
        val input = new HashMap<String, Object>(){{
            put("body", body);
        }};

        return controller.process(input);
    }
}

