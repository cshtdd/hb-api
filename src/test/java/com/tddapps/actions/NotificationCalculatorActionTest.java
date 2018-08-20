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
import org.mockito.invocation.InvocationOnMock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.tddapps.utils.DateExtensions.UtcNowPlusMs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
        HeartBeat hbExpired1 = new HeartBeat("hbExpired1", UtcNowPlusMs(-5000));
        HeartBeat hbExpired2 = new HeartBeat("hbExpired2", UtcNowPlusMs(-15000));
        HeartBeat[] seededHeartBeats = new HeartBeat[]{
                hbExpired1,
                new HeartBeat("hbExpiredTest1", UtcNowPlusMs(-5000), true),
                new HeartBeat("hb1", UtcNowPlusMs(5000)),
                hbExpired2,
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

            String expectedSubject = "Hosts missing [hbExpired1, hbExpired2]";
            String expectedBody = "Hosts missing [hbExpired1, hbExpired2]\n" +
                    "\n" +
                    hbExpired1.toString() +
                    "\n" +
                    hbExpired2.toString() +
                    "\n" +
                    "--";
            verify(notificationSender).Send(expectedBody, expectedSubject);
        } catch (ActionProcessException e) {
            fail("Process should not have thrown", e);
        } catch (DalException e) {
            fail("Send should not have thrown", e);
        }
    }

    @Test
    public void UpdatesTheExpirationOfExpiredHeartBeats(){
        HeartBeat hbExpected1 = new HeartBeat("hbExpired1", UtcNowPlusMs(24 * 60 * 60 * 1000));
        HeartBeat hbExpected2 = new HeartBeat("hbExpired2", UtcNowPlusMs(24 * 60 * 60 * 1000));

        HeartBeat hbExpired1 = new HeartBeat("hbExpired1", UtcNowPlusMs(-5000));
        HeartBeat hbExpired2 = new HeartBeat("hbExpired2", UtcNowPlusMs(-15000));
        HeartBeat[] seededHeartBeats = new HeartBeat[]{
                hbExpired1,
                new HeartBeat("hbExpiredTest1", UtcNowPlusMs(-5000), true),
                new HeartBeat("hb1", UtcNowPlusMs(5000)),
                hbExpired2,
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
            final List<InvocationOnMock> invocations = new ArrayList<>();
            doAnswer(invocations::add)
                    .when(heartBeatRepository)
                    .Save(any(HeartBeat.class));

            action.process();

            assertEquals(2, invocations.size());
            long[] heartBeatTimeUpdates = new long[]{
                    invocations
                            .stream()
                            .map(i -> (HeartBeat) i.getArgument(0))
                            .filter(hbExpected1::almostEquals)
                            .count(),
                    invocations
                            .stream()
                            .map(i -> (HeartBeat) i.getArgument(0))
                            .filter(hbExpected2::almostEquals)
                            .count()
            };
            assertTrue(Arrays.stream(heartBeatTimeUpdates).allMatch(i -> i == 1));
        } catch (ActionProcessException e) {
            fail("Process should not have thrown", e);
        } catch (DalException e) {
            fail("Save should not have thrown", e);
        }
    }
}
