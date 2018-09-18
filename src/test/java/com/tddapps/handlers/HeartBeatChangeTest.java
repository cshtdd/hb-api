package com.tddapps.handlers;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.StreamRecord;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.tddapps.model.DalException;
import com.tddapps.model.Notification;
import com.tddapps.model.NotificationSender;
import lombok.Data;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class HeartBeatChangeTest {
    private final NotificationSender notificationSender = mock(NotificationSender.class);
    private final HeartBeatChange handler = new HeartBeatChange(notificationSender);

    @Data
    private static class HeartBeatEvent{
        final String name;
        final String hostId;
    }

    @Test
    public void CanBeConstructedUsingTheDefaultConstructor(){
        assertNotNull(new HeartBeatChange());
    }

    @Test
    public void SendsANotificationForEachDeletedRecord() throws DalException {
        val result = handleRequest(
                new HeartBeatEvent("MODIFY", "host1"),
                new HeartBeatEvent("INSERT", "host2"),
                new HeartBeatEvent("REMOVE", "host3"),
                new HeartBeatEvent("REMOVE", "host4"),
                new HeartBeatEvent("INSERT", "host5")
        );

        assertTrue(result);
        verify(notificationSender, times(2))
                .Send(any(Notification.class));
        verify(notificationSender).Send(new Notification("Host missing [host3]", "Host missing [host3]"));
        verify(notificationSender).Send(new Notification("Host missing [host4]", "Host missing [host4]"));
    }

    @Test
    public void ReturnsFalseWhenNotificationsCannotBeSent() throws DalException {
        doThrow(new DalException("Send failed"))
                .when(notificationSender)
                .Send(any(Notification.class));

        val result = handleRequest(
                new HeartBeatEvent("REMOVE", "host1")
        );

        assertFalse(result);
    }

    private boolean handleRequest(HeartBeatEvent ... seededEvents){
        List<DynamodbEvent.DynamodbStreamRecord> seededRecords = Arrays.stream(seededEvents)
                .map(e -> {
                    val d = new StreamRecord();
                    d.setKeys(new HashMap<String, AttributeValue>() {{
                        put("host_id", new AttributeValue(e.getHostId()));
                    }});

                    val result = new DynamodbEvent.DynamodbStreamRecord();
                    result.setEventName(e.getName());
                    result.setDynamodb(d);

                    return result;
                })
                .collect(Collectors.toList());

        val input = new DynamodbEvent();
        input.setRecords(seededRecords);

        return handler.handleRequest(input, null);
    }
}
