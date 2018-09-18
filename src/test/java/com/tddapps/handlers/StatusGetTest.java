package com.tddapps.handlers;

import com.tddapps.handlers.infrastructure.ApiGatewayResponse;
import com.tddapps.infrastructure.KeysCacheStub;
import com.tddapps.model.DalException;
import com.tddapps.model.HeartBeat;
import com.tddapps.model.HeartBeatRepository;
import com.tddapps.model.NotificationSenderStatus;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.tddapps.utils.DateExtensions.UtcNowPlusMs;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

public class StatusGetTest {
    private final HeartBeatRepository heartBeatRepository = mock(HeartBeatRepository.class);
    private final NotificationSenderStatus notificationSenderStatus = mock(NotificationSenderStatus.class);
    private final KeysCacheStub keysCache = new KeysCacheStub();
    private final StatusGet handler = new StatusGet(heartBeatRepository, notificationSenderStatus, keysCache);

    private ApiGatewayResponse handleRequest() {
        return handler.handleRequest(new HashMap<>(), null);
    }

    @Test
    public void CanBeConstructedUsingADefaultConstructor(){
        assertNotNull(new StatusGet());
    }

    @Test
    public void VerifiesHeartBeatsCanBeSaved() throws DalException {
        val expectedHeartBeat = new HeartBeat(
                "StatusGet",
                UtcNowPlusMs(4*60*60*1000),
                true
        );


        val result = handleRequest();


        assertEquals(200, result.getStatusCode());
        assertEquals("{\"message\":\"OK\"}", result.getBody());
        verify(heartBeatRepository).Save(argThat(expectedHeartBeat::almostEquals));
    }

    @Test
    public void ProcessThrowsAnActionProcessExceptionWhenTheHeartBeatCouldNotBeSaved() throws DalException {
        doThrow(new DalException("Save failed"))
                .when(heartBeatRepository)
                .Save(any(HeartBeat.class));

        val result = handleRequest();

        assertEquals(500, result.getStatusCode());
        assertEquals("{\"message\":\"Save failed\"}", result.getBody());
    }

    @Test
    public void VerifiesNotificationsCanBeSent() throws DalException{
        handleRequest();

        verify(notificationSenderStatus).Verify();
    }

    @Test
    public void ProcessThrowsAnActionProcessExceptionWhenTheNotificationsCannotBeSent() throws DalException {
        doThrow(new DalException("Sent Notifications Fail"))
                .when(notificationSenderStatus)
                .Verify();

        val result = handleRequest();

        assertEquals(500, result.getStatusCode());
        assertEquals("{\"message\":\"Sent Notifications Fail\"}", result.getBody());
    }

    @Test
    public void ProcessCachesResult() throws DalException {
        final List<InvocationOnMock> invocations = new ArrayList<>();
        doAnswer(invocations::add)
                .when(heartBeatRepository)
                .Save(any(HeartBeat.class));

        handleRequest();
        handleRequest();

        assertTrue(keysCache.Contains(StatusGet.class.getName()));
        assertEquals(1, invocations.size());

        keysCache.getKeys().clear();
        handleRequest();

        assertTrue(keysCache.Contains(StatusGet.class.getName()));
        assertEquals(2, invocations.size());
    }

    @Test
    public void ProcessDoesNotCacheFailures() throws DalException {
        doThrow(new DalException("Save failed"))
                .when(heartBeatRepository)
                .Save(any(HeartBeat.class));

        handleRequest();

        assertFalse(keysCache.Contains(StatusGet.class.getName()));
    }

}
