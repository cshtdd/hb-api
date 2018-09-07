package com.tddapps.dal;

import org.junit.jupiter.api.Test;

import static com.tddapps.utils.DateExtensions.*;
import static org.junit.jupiter.api.Assertions.*;

public class SingleNotificationBuilderTest {
    private final SingleNotificationBuilder builder = new SingleNotificationBuilder();

    @Test
    public void DoesNotSendNotificationWhenNoHeartBeatsAreProvided(){
        assertEquals(0, builder.build(null).length);
        assertEquals(0, builder.build(new HeartBeat[]{}).length);
    }

    @Test
    public void SendsNotificationForASingleHeartBeat(){
        HeartBeat hb1 = new HeartBeat("host1", UtcNow());
        HeartBeat[] input = new HeartBeat[]{
                hb1
        };

        Notification[] notifications = builder.build(input);
        assertEquals(1, notifications.length);
        Notification notification = notifications[0];

        assertEquals("Hosts missing [host1]", notification.getSubject());
        String expectedBody = "Hosts missing [host1]\n" +
                "\n" +
                hb1.toString() +
                "\n" +
                "--";
        assertEquals(expectedBody, notification.getMessage());
    }

    @Test
    public void SendsSingleNotificationForMultipleHeartBeats(){
        HeartBeat hb1 = new HeartBeat("host1", UtcNow());
        HeartBeat hb2 = new HeartBeat("host2", UtcNow());
        HeartBeat[] input = new HeartBeat[]{
                hb1,
                hb2
        };

        Notification[] notifications = builder.build(input);
        assertEquals(1, notifications.length);
        Notification notification = notifications[0];

        assertEquals("Hosts missing [host1, host2]", notification.getSubject());
        String expectedBody = "Hosts missing [host1, host2]\n" +
                "\n" +
                hb1.toString() +
                "\n" +
                hb2.toString() +
                "\n" +
                "--";
        assertEquals(expectedBody, notification.getMessage());
    }
}
