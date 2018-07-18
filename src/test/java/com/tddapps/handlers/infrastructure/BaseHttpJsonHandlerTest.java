package com.tddapps.handlers.infrastructure;

import com.amazonaws.services.lambda.runtime.Context;
import com.tddapps.actions.response.TextMessage;
import com.tddapps.controllers.HttpJsonController;
import com.tddapps.controllers.HttpJsonResponse;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BaseHttpJsonHandlerTest {
    private final HttpJsonController controllerMock = mock(HttpJsonController.class);
    private final Context seededContext = mock(Context.class);
    private final HttpJsonHandlerStub handler = new HttpJsonHandlerStub(controllerMock);

    @Test
    public void ReturnsTheControllerResponse(){
        Map<String, Object> input = new HashMap<String, Object>(){{
            put("AAA", "AAA");
        }};

        when(controllerMock.process(input)).thenReturn(new HttpJsonResponse<>(
                200, TextMessage.create("pepe")
        ));

        ApiGatewayResponse response = handler.handleRequest(input, seededContext);

        assertEquals(200, response.getStatusCode());
        assertEquals("{\"message\":\"pepe\"}", response.getBody());
    }
}

