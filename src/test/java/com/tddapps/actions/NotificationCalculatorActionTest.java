package com.tddapps.actions;

import com.tddapps.actions.response.TextMessage;
import com.tddapps.controllers.ActionProcessException;
import com.tddapps.controllers.HttpJsonResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class NotificationCalculatorActionTest {
    private final NotificationCalculatorAction action = new NotificationCalculatorAction();

    @Test
    public void ReturnsSuccess(){
        try {
            HttpJsonResponse<TextMessage> result = action.process();

            assertEquals(HttpJsonResponse.Success(TextMessage.OK), result);
        } catch (ActionProcessException e) {
            fail("Process should not have thrown", e);
        }
    }
}
