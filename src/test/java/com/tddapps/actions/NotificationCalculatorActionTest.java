package com.tddapps.actions;

import com.tddapps.actions.response.TextMessage;
import com.tddapps.controllers.ActionProcessException;
import com.tddapps.controllers.HttpJsonResponse;
import com.tddapps.dal.DalException;
import com.tddapps.dal.HeartBeat;
import com.tddapps.dal.HeartBeatRepository;
import com.tddapps.dal.NotificationSender;
import org.junit.jupiter.api.Test;
import org.mockito.internal.verification.Times;

import static com.tddapps.utils.DateExtensions.UtcNowPlusMs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

public class NotificationCalculatorActionTest {
    private final HeartBeatRepository heartBeatRepository = mock(HeartBeatRepository.class);
    private final NotificationSender notificationSender = mock(NotificationSender.class);
    private final NotificationCalculatorAction action = new NotificationCalculatorAction(heartBeatRepository, notificationSender);

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

    @Test
    public void SendsNotificationsForEachExpiredHeartBeat(){
        HeartBeat[] seededHeartBeats = new HeartBeat[]{
                new HeartBeat("hbExpired1", UtcNowPlusMs(-5000)),
                new HeartBeat("hbExpiredTest1", UtcNowPlusMs(-5000), true),
                new HeartBeat("hb1", UtcNowPlusMs(5000)),
                new HeartBeat("hbExpired2", UtcNowPlusMs(-15000)),
                new HeartBeat("hb2", UtcNowPlusMs(25000))
        };
        try {
            doReturn(seededHeartBeats)
                    .when(heartBeatRepository)
                    .All();
        } catch (DalException e) {
            fail("All should not have thrown", e);
        }

        try {
            action.process();

            verify(notificationSender, times(2))
                    .Send(any(String.class), any(String.class));
            verify(notificationSender)
                    .Send("Host hbExpired1 missing", "Host hbExpired1 missing");
            verify(notificationSender)
                    .Send("Host hbExpired2 missing", "Host hbExpired2 missing");
        } catch (ActionProcessException e) {
            fail("Process should not have thrown", e);
        } catch (DalException e) {
            fail("Send should not have thrown", e);
        }
    }
}
