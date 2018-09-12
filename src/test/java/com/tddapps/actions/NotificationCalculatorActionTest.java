package com.tddapps.actions;

import com.tddapps.actions.response.TextMessage;
import com.tddapps.controllers.ActionProcessException;
import com.tddapps.controllers.HttpJsonResponse;
import com.tddapps.model.*;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.tddapps.utils.DateExtensions.UtcNowPlusMs;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NotificationCalculatorActionTest {
    private final HeartBeatRepository heartBeatRepository = mock(HeartBeatRepository.class);
    private final NotificationSender notificationSender = mock(NotificationSender.class);
    private final HeartBeatNotificationBuilder notificationBuilder = new HeartBeatNotificationBuilderOneToOneStub();
    private final NotificationCalculatorAction action = new NotificationCalculatorAction(
            heartBeatRepository,
            notificationBuilder,
            notificationSender
    );

    @Test
    public void ReadsAllTheHeartBeats() throws ActionProcessException, DalException {
        val result = action.process();

        verify(heartBeatRepository).All();
        assertEquals(HttpJsonResponse.Success(TextMessage.OK), result);
    }

    @Test
    public void ProcessThrowsAnActionProcessExceptionWhenHeartBeatsCouldNotBeRead() throws DalException {
        doThrow(new DalException("All failed"))
                .when(heartBeatRepository)
                .All();

        String actualMessage = "";

        try {
            action.process();
            fail("Process Should have thrown an error");
        } catch (ActionProcessException e) {
            actualMessage = e.getMessage();
        }

        assertEquals("All failed", actualMessage);
    }

    @Test
    public void SendsNotificationsForEachExpiredHeartBeat() throws DalException, ActionProcessException {
        val hbExpired1 = new HeartBeat("hbExpired1", UtcNowPlusMs(-5000), false);
        val hbExpired2 = new HeartBeat("hbExpired2", UtcNowPlusMs(-15000), false);
        val seededHeartBeats = new HeartBeat[]{
                hbExpired1,
                new HeartBeat("hbExpiredTest1", UtcNowPlusMs(-5000), true),
                new HeartBeat("hb1", UtcNowPlusMs(5000), false),
                hbExpired2,
                new HeartBeat("hb2", UtcNowPlusMs(25000), false)
        };
        doReturn(seededHeartBeats)
                .when(heartBeatRepository)
                .All();


        action.process();


        verify(notificationSender).Send(new Notification("S-hbExpired1", "M-hbExpired1"));
        verify(notificationSender).Send(new Notification("S-hbExpired2", "M-hbExpired2"));
    }

    @Test
    public void ProcessThrowsAnActionProcessExceptionWhenNotificationsCouldNotBeSent() throws DalException {
        val seededHeartBeats = new HeartBeat[]{
                new HeartBeat("hbExpired1", UtcNowPlusMs(-5000), false)
        };
        doReturn(seededHeartBeats)
                .when(heartBeatRepository)
                .All();
        doThrow(new DalException("Send failed"))
                .when(notificationSender)
                .Send(any(Notification.class));

        String actualMessage = "";

        try {
            action.process();
            fail("Process Should have thrown an error");
        } catch (ActionProcessException e) {
            actualMessage = e.getMessage();
        }

        assertEquals("Send failed", actualMessage);
        verify(heartBeatRepository, times(0))
                .Save(any(HeartBeat.class));
        verify(heartBeatRepository, times(0))
                .Save(any(HeartBeat[].class));
    }

    @Test
    public void UpdatesTheExpirationOfExpiredHeartBeats() throws DalException, ActionProcessException {
        val hbExpected1 = new HeartBeat("hbExpired1", UtcNowPlusMs(24 * 60 * 60 * 1000), false);
        val hbExpected2 = new HeartBeat("hbExpired2", UtcNowPlusMs(24 * 60 * 60 * 1000), false);
        HeartBeat[] expectedUpdates = { hbExpected1, hbExpected2 };

        val hbExpired1 = new HeartBeat("hbExpired1", UtcNowPlusMs(-5000), false);
        val hbExpired2 = new HeartBeat("hbExpired2", UtcNowPlusMs(-15000), false);
        val seededHeartBeats = new HeartBeat[]{
                hbExpired1,
                new HeartBeat("hbExpiredTest1", UtcNowPlusMs(-5000), true),
                new HeartBeat("hb1", UtcNowPlusMs(5000), false),
                hbExpired2,
                new HeartBeat("hb2", UtcNowPlusMs(25000), false)
        };
        doReturn(seededHeartBeats)
                .when(heartBeatRepository)
                .All();

        final List<InvocationOnMock> invocations = new ArrayList<>();
        doAnswer(invocations::add)
                .when(heartBeatRepository)
                .Save(any(HeartBeat[].class));


        action.process();


        assertEquals(1, invocations.size());
        HeartBeatListHelper.ShouldMatch(expectedUpdates, invocations.get(0).getArgument(0));
        verify(heartBeatRepository, times(0))
                .Save(any(HeartBeat.class));
    }

    @Test
    public void DoesNotSendNotificationWhenNoHeartBeatsExpired() throws ActionProcessException, DalException {
        val seededHeartBeats = new HeartBeat[]{
                new HeartBeat("hbExpiredTest1", UtcNowPlusMs(-5000), true),
                new HeartBeat("hb1", UtcNowPlusMs(5000), false),
                new HeartBeat("hb2", UtcNowPlusMs(25000), false)
        };
        doReturn(seededHeartBeats)
                .when(heartBeatRepository)
                .All();

        action.process();

        verify(notificationSender, times(0))
                .Send(any(Notification.class));
    }

    @Test
    public void DoesNotUpdatedHeartBeatsWhenNoHeartBeatsExpired() throws DalException, ActionProcessException {
        val seededHeartBeats = new HeartBeat[]{
                new HeartBeat("hbExpiredTest1", UtcNowPlusMs(-5000), true),
                new HeartBeat("hb1", UtcNowPlusMs(5000), false),
                new HeartBeat("hb2", UtcNowPlusMs(25000), false)
        };
        doReturn(seededHeartBeats)
                .when(heartBeatRepository)
                .All();

        action.process();

        verify(heartBeatRepository, times(0))
                .Save(any(HeartBeat.class));
    }
}
