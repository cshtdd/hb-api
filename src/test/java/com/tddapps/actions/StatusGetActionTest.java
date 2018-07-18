package com.tddapps.actions;

import com.tddapps.actions.response.TextMessage;
import com.tddapps.controllers.ActionProcessException;
import com.tddapps.controllers.HttpJsonResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StatusGetActionTest {
    private final StatusGetAction action = new StatusGetAction();

    @Test
    public void ReturnsOk(){
        assertEquals(HttpJsonResponse.Success(TextMessage.OK), process());
    }

    private HttpJsonResponse<TextMessage> process(){
        try {
            return action.process();
        } catch (ActionProcessException e) {
            fail("Process should not have thrown", e);
            return null;
        }
    }
}
