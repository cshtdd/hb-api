package com.tddapps.model;

import com.tddapps.utils.NowReader;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.tddapps.model.HeartBeatFactory.TEST_REGION_DEFAULT;
import static com.tddapps.utils.DateExtensions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NotificationBuilderGroupedTest {
    private final NowReader nowReaderMock = mock(NowReader.class);
    private final NotificationBuilderGrouped builder = new NotificationBuilderGrouped(nowReaderMock);

    private String utcNowFormatted;

    @BeforeEach
    public void Setup(){
        val seededDate = UtcNowPlusMs(1000);
        utcNowFormatted = ToUtcString(seededDate);

        when(nowReaderMock.ReadUtc())
                .thenReturn(seededDate);
    }

    @Test
    public void DoesNotAllowANullInput(){
        try{
            builder.build((HeartBeatChangeEvent[]) null);
            fail("Should have thrown");
        }catch (NullPointerException e){
            assertNotNull(e);
        }
    }

    @Test
    public void DoesNotSendNotificationWhenNoHeartBeatsAreProvided(){
        assertEquals(0, builder.build(new HeartBeatChangeEvent[]{}).length);
    }

    @Test
    public void SendsNotificationForASingleEvent() {
        val hb1 = new HeartBeat("host1", EpochSecondsNow(), ToReverseUtcMinuteString(EpochSecondsNow()), TEST_REGION_DEFAULT, false);
        val event1 = new HeartBeatChangeEvent("deleted", hb1);

        val notifications = builder.build(new HeartBeatChangeEvent[]{event1});
        assertEquals(1, notifications.length);
        val notification = notifications[0];

        assertEquals("deleted [host1]", notification.getSubject());
        val expectedBody = "deleted [host1]\n" +
                "\n" +
                hb1.toString() +
                "\n" +
                "--" +
                "\n" +
                "Notification Built: " + utcNowFormatted +
                "\n" +
                "--";
        assertEquals(expectedBody, notification.getMessage());
    }

    @Test
    public void GroupsNotificationsByEventType(){
        val input = new HeartBeatChangeEvent[]{
                new HeartBeatChangeEvent("deleted", HeartBeatFactory.Create("host1")),
                new HeartBeatChangeEvent("deleted", HeartBeatFactory.Create("host2")),
                new HeartBeatChangeEvent("created", HeartBeatFactory.Create("host3")),
                new HeartBeatChangeEvent("created", HeartBeatFactory.Create("host4"))
        };

        val notifications = builder.build(input);
        assertEquals(2, notifications.length);

        val hostsMissingNotification = notifications[0];
        assertEquals("deleted [host1, host2]", hostsMissingNotification.getSubject());
        val expectedBody1 = "deleted [host1, host2]\n" +
                "\n" +
                input[0].getHeartBeat().toString() +
                "\n" +
                input[1].getHeartBeat().toString() +
                "\n" +
                "--" +
                "\n" +
                "Notification Built: " + utcNowFormatted +
                "\n" +
                "--";
        assertEquals(expectedBody1, hostsMissingNotification.getMessage());

        val hostsRegisteredNotification = notifications[1];
        assertEquals("created [host3, host4]", hostsRegisteredNotification.getSubject());
        val expectedBody2 = "created [host3, host4]\n" +
                "\n" +
                input[2].getHeartBeat().toString() +
                "\n" +
                input[3].getHeartBeat().toString() +
                "\n" +
                "--" +
                "\n" +
                "Notification Built: " + utcNowFormatted +
                "\n" +
                "--";
        assertEquals(expectedBody2, hostsRegisteredNotification.getMessage());
    }
}
