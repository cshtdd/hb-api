package com.tddapps.actions;

import com.tddapps.actions.response.TextMessage;
import com.tddapps.controllers.ActionProcessException;
import com.tddapps.controllers.HttpJsonResponse;
import com.tddapps.dal.DalException;
import com.tddapps.dal.HeartBeatRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class NotificationCalculatorActionTest {
    private final HeartBeatRepository heartBeatRepository = mock(HeartBeatRepository.class);
    private final NotificationCalculatorAction action = new NotificationCalculatorAction(heartBeatRepository);

    @Test
    public void ReadsAllTheHeartBeats(){
        try {
            HttpJsonResponse<TextMessage> result = action.process();

            verify(heartBeatRepository).All();
            assertEquals(HttpJsonResponse.Success(TextMessage.OK), result);
        } catch (ActionProcessException e) {
            fail("Process should not have thrown", e);
        } catch (DalException e) {
            fail("All should not have thrown", e);
        }
    }

    @Test
    public void ProcessThrowsAnActionProcessExceptionWhenHeartBeatsCouldNotBeRead(){
        try {
            doThrow(new DalException("All failed"))
                    .when(heartBeatRepository)
                    .All();

            action.process();
            fail("Process Should have thrown an error");
        } catch (ActionProcessException e) {
            assertEquals("All failed", e.getMessage());
        } catch (DalException e) {
            fail("All should not have thrown", e);
        }
    }
}
