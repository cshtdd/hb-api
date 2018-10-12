package com.tddapps.handlers;

import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.tddapps.model.*;
import com.tddapps.model.aws.DynamoDBEventParser;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static com.tddapps.model.HeartBeatFactory.TEST_REGION_DEFAULT;
import static com.tddapps.utils.DateExtensions.EpochSecondsNow;
import static com.tddapps.utils.DateExtensions.ToReverseUtcMinuteString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class HeartBeatChangeTest {
    private final NotificationSender notificationSender = mock(NotificationSender.class);
    private final RequestHandlerHelper requestHandlerHelper = mock(RequestHandlerHelper.class);
    private final HeartBeatNotificationBuilderOneToOneStub notificationBuilder = new HeartBeatNotificationBuilderOneToOneStub();
    private final DynamoDBEventParser eventParser = mock(DynamoDBEventParser.class);
    private final HeartBeatChange handler = new HeartBeatChange(
            notificationBuilder,
            notificationSender,
            requestHandlerHelper,
            eventParser
    );

    private final DynamodbEvent seededInput = new DynamodbEvent();

    private final long ttlNow = EpochSecondsNow();
    private final String reversedUtcMinuteNowString = ToReverseUtcMinuteString(ttlNow);

    @Test
    public void CanBeConstructedUsingTheDefaultConstructor(){
        assertNotNull(new HeartBeatChange());
    }

    @Test
    public void SendsANotificationForEachDeletedOrInsertedRecord() throws DalException {
        when(requestHandlerHelper.filter(any())).then(i -> i.getArgument(0));
        when(eventParser.readDeletions(seededInput, HeartBeat.class)).thenReturn(new ArrayList<HeartBeat>(){{
            add(new HeartBeat("host3", ttlNow, reversedUtcMinuteNowString, TEST_REGION_DEFAULT, false));
            add(new HeartBeat("host4", ttlNow, reversedUtcMinuteNowString, TEST_REGION_DEFAULT, false));
        }});
        when(eventParser.readInsertions(seededInput, HeartBeat.class)).thenReturn(new ArrayList<HeartBeat>(){{
            add(new HeartBeat("host2", ttlNow, reversedUtcMinuteNowString, TEST_REGION_DEFAULT, false));
            add(new HeartBeat("host5", ttlNow, reversedUtcMinuteNowString, TEST_REGION_DEFAULT, false));
            add(new HeartBeat("host6", ttlNow, reversedUtcMinuteNowString, TEST_REGION_DEFAULT, false));
        }});

        assertTrue(run());

        verify(notificationSender, times(5))
                .Send(any(Notification.class));
        verify(notificationSender).Send(new Notification("S-host2", "M-host2-Hosts registered"));
        verify(notificationSender).Send(new Notification("S-host3", "M-host3-Hosts missing"));
        verify(notificationSender).Send(new Notification("S-host4", "M-host4-Hosts missing"));
        verify(notificationSender).Send(new Notification("S-host5", "M-host5-Hosts registered"));
        verify(notificationSender).Send(new Notification("S-host6", "M-host6-Hosts registered"));
    }

    @Test
    public void SendsNotificationOnlyForRecordsInTheCurrentRegion() throws DalException {
        when(requestHandlerHelper.filter(any())).then(i -> {
            HeartBeat[] heartBeats = i.getArgument(0);
            return Arrays.stream(heartBeats)
                    .filter(hb -> hb.getRegion().equals(TEST_REGION_DEFAULT))
                    .toArray(HeartBeat[]::new);
        });
        when(eventParser.readDeletions(seededInput, HeartBeat.class)).thenReturn(new ArrayList<HeartBeat>(){{
            add(new HeartBeat("host1", ttlNow, reversedUtcMinuteNowString, TEST_REGION_DEFAULT, false));
            add(new HeartBeat("host2", ttlNow, reversedUtcMinuteNowString, "us-test-2", false));
            add(new HeartBeat("host3", ttlNow, reversedUtcMinuteNowString, "us-test-2", false));
            add(new HeartBeat("host4", ttlNow, reversedUtcMinuteNowString, TEST_REGION_DEFAULT, false));
        }});
        when(eventParser.readInsertions(seededInput, HeartBeat.class)).thenReturn(new ArrayList<HeartBeat>(){{
            add(new HeartBeat("host5", ttlNow, reversedUtcMinuteNowString, TEST_REGION_DEFAULT, false));
            add(new HeartBeat("host6", ttlNow, reversedUtcMinuteNowString, "us-test-2", false));
            add(new HeartBeat("host7", ttlNow, reversedUtcMinuteNowString, "us-test-2", false));
            add(new HeartBeat("host8", ttlNow, reversedUtcMinuteNowString, TEST_REGION_DEFAULT, false));
        }});

        assertTrue(run());

        verify(notificationSender, times(4))
                .Send(any(Notification.class));
        verify(notificationSender).Send(new Notification("S-host1", "M-host1-Hosts missing"));
        verify(notificationSender).Send(new Notification("S-host4", "M-host4-Hosts missing"));
        verify(notificationSender).Send(new Notification("S-host5", "M-host5-Hosts registered"));
        verify(notificationSender).Send(new Notification("S-host8", "M-host8-Hosts registered"));
    }

    @Test
    public void DoesNotSendNotificationForTestRecords() throws DalException {
        when(requestHandlerHelper.filter(any())).then(i -> i.getArgument(0));
        when(eventParser.readDeletions(seededInput, HeartBeat.class)).thenReturn(new ArrayList<HeartBeat>(){{
            add(new HeartBeat("host1", ttlNow, reversedUtcMinuteNowString, TEST_REGION_DEFAULT, true));
        }});
        when(eventParser.readInsertions(seededInput, HeartBeat.class)).thenReturn(new ArrayList<HeartBeat>(){{
            add(new HeartBeat("host5", ttlNow, reversedUtcMinuteNowString, TEST_REGION_DEFAULT, true));
        }});

        assertTrue(run());

        verify(notificationSender, times(0))
                .Send(any(Notification.class));
    }

    @Test
    public void ReturnsFalseWhenNotificationsCannotBeSent() throws DalException {
        doThrow(new DalException("Send failed"))
                .when(notificationSender)
                .Send(any(Notification.class));

        when(requestHandlerHelper.filter(any())).then(i -> i.getArgument(0));
        when(eventParser.readDeletions(seededInput, HeartBeat.class)).thenReturn(new ArrayList<HeartBeat>(){{
            add(new HeartBeat("host1", ttlNow, reversedUtcMinuteNowString, TEST_REGION_DEFAULT, false));
        }});

        assertFalse(run());
    }

    private boolean run(){
        return handler.handleRequest(seededInput, null);
    }
}
