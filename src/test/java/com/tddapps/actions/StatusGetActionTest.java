package com.tddapps.actions;

import com.tddapps.actions.response.TextMessage;
import com.tddapps.controllers.ActionProcessException;
import com.tddapps.controllers.HttpJsonResponse;
import com.tddapps.dal.DalException;
import com.tddapps.dal.HeartBeat;
import com.tddapps.dal.HeartBeatRepository;
import org.junit.jupiter.api.Test;

import static com.tddapps.utils.DateExtensions.UtcNowPlusMs;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class StatusGetActionTest {
    private final HeartBeatRepository heartBeatRepository = mock(HeartBeatRepository.class);
    private final StatusGetAction action = new StatusGetAction(heartBeatRepository);

    @Test
    public void VerifiesHeartBeatsCanBeSaved(){
        HeartBeat expectedHeartBeat = new HeartBeat(
                "StatusGetAction",
                UtcNowPlusMs(4*60*60*1000),
                true
        );

        try {
            HttpJsonResponse<TextMessage> result = action.process();

            assertEquals(HttpJsonResponse.Success(TextMessage.OK), result);
            verify(heartBeatRepository).Save(argThat(t -> t.almostEquals(expectedHeartBeat)));
        } catch (ActionProcessException e) {
            fail("Process should not have thrown", e);
        } catch (DalException e) {
            fail("Save should not have thrown", e);
        }
    }

    @Test
    public void ProcessThrowsAnActionProcessExceptionWhenTheHeartBeatCouldNotBeSaved(){
        try {
            doThrow(new DalException("Save failed"))
                    .when(heartBeatRepository)
                    .Save(any(HeartBeat.class));

            action.process();
            fail("Process Should have thrown an error");
        } catch (ActionProcessException e) {
            assertEquals("Save failed", e.getMessage());
        } catch (DalException e) {
            fail("Save should not have thrown", e);
        }
    }

}
