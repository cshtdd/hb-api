package com.tddapps.actions;

import com.tddapps.actions.response.TextMessage;
import com.tddapps.controllers.ActionProcessException;
import com.tddapps.controllers.HttpJsonResponse;
import com.tddapps.model.DalException;
import com.tddapps.model.HeartBeat;
import com.tddapps.model.HeartBeatRepository;
import com.tddapps.infrastructure.KeysCacheStub;
import com.tddapps.model.NotificationSenderStatus;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;

import java.util.ArrayList;
import java.util.List;

import static com.tddapps.utils.DateExtensions.UtcNowPlusMs;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

public class StatusGetActionTest {
    private final HeartBeatRepository heartBeatRepository = mock(HeartBeatRepository.class);
    private final NotificationSenderStatus notificationSenderStatus = mock(NotificationSenderStatus.class);
    private final KeysCacheStub keysCache = new KeysCacheStub();
    private final StatusGetAction action = new StatusGetAction(heartBeatRepository, notificationSenderStatus, keysCache);

    @Test
    public void VerifiesHeartBeatsCanBeSaved() throws ActionProcessException, DalException {
        val expectedHeartBeat = new HeartBeat(
                "StatusGetAction",
                UtcNowPlusMs(4*60*60*1000),
                true
        );


        val result = action.process();


        assertEquals(HttpJsonResponse.Success(TextMessage.OK), result);
        verify(heartBeatRepository).Save(argThat(t -> t.almostEquals(expectedHeartBeat)));
    }

    @Test
    public void ProcessThrowsAnActionProcessExceptionWhenTheHeartBeatCouldNotBeSaved() throws DalException {
        doThrow(new DalException("Save failed"))
                .when(heartBeatRepository)
                .Save(any(HeartBeat.class));

        String actualMessage = "";

        try {
            action.process();
            fail("Process Should have thrown an error");
        } catch (ActionProcessException e) {
            actualMessage = e.getMessage();
        }

        assertEquals("Save failed", actualMessage);
    }

    @Test
    public void VerifiesNotificationsCanBeSent() throws ActionProcessException, DalException{
        action.process();

        verify(notificationSenderStatus).Verify();
    }

    @Test
    public void ProcessThrowsAnActionProcessExceptionWhenTheNotificationsCannotBeSent() throws DalException {
        doThrow(new DalException("Sent Notifications Fail"))
                .when(notificationSenderStatus)
                .Verify();

        String actualMessage = "";

        try{
            action.process();
            fail("Process Should have thrown an error");
        } catch (ActionProcessException e){
            actualMessage = e.getMessage();
        }

        assertEquals("Sent Notifications Fail", actualMessage);
    }

    @Test
    public void ProcessCachesResult() throws ActionProcessException, DalException {
        final List<InvocationOnMock> invocations = new ArrayList<>();
        doAnswer(invocations::add)
                .when(heartBeatRepository)
                .Save(any(HeartBeat.class));

        action.process();
        action.process();

        assertTrue(keysCache.Contains(StatusGetAction.class.getName()));
        assertEquals(1, invocations.size());

        keysCache.getKeys().clear();
        action.process();

        assertTrue(keysCache.Contains(StatusGetAction.class.getName()));
        assertEquals(2, invocations.size());
    }

    @Test
    public void ProcessDoesNotCacheFailures() throws DalException, ActionProcessException {
        doThrow(new DalException("Save failed"))
                .when(heartBeatRepository)
                .Save(any(HeartBeat.class));

        try {
            action.process();
            fail("Process Should have thrown an error");
        } catch (ActionProcessException e) {}


        assertFalse(keysCache.Contains(StatusGetAction.class.getName()));
    }
}
