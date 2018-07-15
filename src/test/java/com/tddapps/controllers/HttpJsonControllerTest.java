package com.tddapps.controllers;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


public class HttpJsonControllerTest {
    private final HttpJsonAction actionMock = mock(HttpJsonAction.class);
    private final HttpJsonController controller = new HttpJsonController(actionMock);

    @Test
    public void ReturnsBadRequestWhenBodyMissing(){
        Map<String, Object> input = new HashMap<String, Object>(){{
            put("key1", "value1");
        }};

        assertEquals(HttpJsonResponse.BadRequestWithMessage("Empty Request Body"), controller.process(input));
    }

    @Test
    public void ReturnsBadRequestWhenBodyIsNull(){
        Map<String, Object> input = new HashMap<String, Object>(){{
            put("body", null);
        }};

        assertEquals(HttpJsonResponse.BadRequestWithMessage("Empty Request Body"), controller.process(input));
    }

    @Test
    public void ReturnsBadRequestWhenBodyIsEmpty(){
        Map<String, Object> input = new HashMap<String, Object>(){{
            put("body", "");
        }};

        assertEquals(HttpJsonResponse.BadRequestWithMessage("Empty Request Body"), controller.process(input));
    }

    @Test
    public void ReturnsBadRequestWhenBodyIsBlank(){
        Map<String, Object> input = new HashMap<String, Object>(){{
            put("body", "   ");
        }};

        assertEquals(HttpJsonResponse.BadRequestWithMessage("Empty Request Body"), controller.process(input));
    }
}
