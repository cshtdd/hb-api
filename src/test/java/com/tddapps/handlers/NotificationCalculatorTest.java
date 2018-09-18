package com.tddapps.handlers;

import com.tddapps.model.*;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;

import java.util.ArrayList;
import java.util.List;

import static com.tddapps.utils.DateExtensions.UtcNowPlusMs;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class NotificationCalculatorTest {
    private final HeartBeatRepository heartBeatRepository = mock(HeartBeatRepository.class);
    private final NotificationSender notificationSender = mock(NotificationSender.class);
    private final HeartBeatNotificationBuilder notificationBuilder = new HeartBeatNotificationBuilderOneToOneStub();
    private final NotificationCalculator handler = new NotificationCalculator(
            heartBeatRepository,
            notificationBuilder,
            notificationSender
    );

    private boolean handleRequest(){
        return handler.handleRequest(true, null);
    }

    @Test
    public void CanBeConstructedUsingADefaultConstructor(){
        assertNotNull(new NotificationCalculator());
    }

    @Test
    public void ReadsAllTheHeartBeats() throws DalException {
        val result = handleRequest();

        verify(heartBeatRepository).All();
        assertTrue(result);
    }

    @Test
    public void ProcessThrowsAnActionProcessExceptionWhenHeartBeatsCouldNotBeRead() throws DalException {
        doThrow(new DalException("All failed"))
                .when(heartBeatRepository)
                .All();

        val result = handleRequest();

        assertFalse(result);
    }

    @Test
    public void SendsNotificationsForEachExpiredHeartBeat() throws DalException {
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

        val result = handleRequest();

        assertTrue(result);
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

        val result = handleRequest();

        assertFalse(result);
        verify(heartBeatRepository, times(0))
                .Save(any(HeartBeat.class));
        verify(heartBeatRepository, times(0))
                .Save(any(HeartBeat[].class));
    }

    @Test
    public void UpdatesTheExpirationOfExpiredHeartBeats() throws DalException {
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


        val result = handleRequest();


        assertTrue(result);
        assertEquals(1, invocations.size());
        HeartBeatListHelper.ShouldMatch(expectedUpdates, invocations.get(0).getArgument(0));
        verify(heartBeatRepository, times(0))
                .Save(any(HeartBeat.class));
    }

    @Test
    public void DoesNotSendNotificationWhenNoHeartBeatsExpired() throws DalException {
        val seededHeartBeats = new HeartBeat[]{
                new HeartBeat("hbExpiredTest1", UtcNowPlusMs(-5000), true),
                new HeartBeat("hb1", UtcNowPlusMs(5000), false),
                new HeartBeat("hb2", UtcNowPlusMs(25000), false)
        };
        doReturn(seededHeartBeats)
                .when(heartBeatRepository)
                .All();

        val result = handleRequest();

        assertTrue(result);
        verify(notificationSender, times(0))
                .Send(any(Notification.class));
    }

    @Test
    public void DoesNotUpdatedHeartBeatsWhenNoHeartBeatsExpired() throws DalException {
        val seededHeartBeats = new HeartBeat[]{
                new HeartBeat("hbExpiredTest1", UtcNowPlusMs(-5000), true),
                new HeartBeat("hb1", UtcNowPlusMs(5000), false),
                new HeartBeat("hb2", UtcNowPlusMs(25000), false)
        };
        doReturn(seededHeartBeats)
                .when(heartBeatRepository)
                .All();

        val result = handleRequest();

        assertTrue(result);
        verify(heartBeatRepository, times(0))
                .Save(any(HeartBeat.class));
    }
}
