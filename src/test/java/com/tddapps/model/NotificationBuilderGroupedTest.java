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
            builder.build((HeartBeat[]) null);
            fail("Should have thrown");
        }catch (NullPointerException e){
            assertNotNull(e);
        }
    }

    @Test
    public void DoesNotSendNotificationWhenNoHeartBeatsAreProvided(){
        assertEquals(0, builder.build(new HeartBeat[]{}).length);
    }

    @Test
    public void SendsNotificationForASingleExpiredHeartBeat(){
        long ttlInThePast = EpochSecondsPlusMs(-2000);
        val hb1 = new HeartBeat("host1", ttlInThePast, ToReverseUtcMinuteString(ttlInThePast), TEST_REGION_DEFAULT, false);
        val input = new HeartBeat[]{
                hb1
        };

        val notifications = builder.build(input);
        assertEquals(1, notifications.length);
        val notification = notifications[0];

        assertEquals("Hosts missing [host1]", notification.getSubject());
        val expectedBody = "Hosts missing [host1]\n" +
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
    public void SendsNotificationForASingleNotExpiredHeartBeat(){
        long ttlInThePast = EpochSecondsPlusMs(2000);
        val hb1 = new HeartBeat("host1", ttlInThePast, ToReverseUtcMinuteString(ttlInThePast), TEST_REGION_DEFAULT, false);
        val input = new HeartBeat[]{
                hb1
        };

        val notifications = builder.build(input);
        assertEquals(1, notifications.length);
        val notification = notifications[0];

        assertEquals("Hosts registered [host1]", notification.getSubject());
        val expectedBody = "Hosts registered [host1]\n" +
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
    public void SendsSingleNotificationForMultipleHeartBeats(){
        long ttlInThePast = EpochSecondsPlusMs(-2000);
        long ttlInTheFuture = EpochSecondsPlusMs(2000);
        val hb1 = new HeartBeat("host1", ttlInThePast, ToReverseUtcMinuteString(ttlInThePast), "us-test-2", false);
        val hb2 = new HeartBeat("host2", ttlInThePast, ToReverseUtcMinuteString(ttlInThePast), "us-test-2", false);
        val hb3 = new HeartBeat("host3", ttlInTheFuture, ToReverseUtcMinuteString(ttlInTheFuture), "us-test-2", false);
        val hb4 = new HeartBeat("host4", ttlInTheFuture, ToReverseUtcMinuteString(ttlInTheFuture), "us-test-2", false);
        val input = new HeartBeat[]{ hb3, hb1, hb2, hb4 };

        val notifications = builder.build(input);
        assertEquals(2, notifications.length);

        val hostsMissingNotification = notifications[0];
        assertEquals("Hosts missing [host1, host2]", hostsMissingNotification.getSubject());
        val expectedBody1 = "Hosts missing [host1, host2]\n" +
                "\n" +
                hb1.toString() +
                "\n" +
                hb2.toString() +
                "\n" +
                "--" +
                "\n" +
                "Notification Built: " + utcNowFormatted +
                "\n" +
                "--";
        assertEquals(expectedBody1, hostsMissingNotification.getMessage());

        val hostsRegisteredNotification = notifications[1];
        assertEquals("Hosts registered [host3, host4]", hostsRegisteredNotification.getSubject());
        val expectedBody2 = "Hosts registered [host3, host4]\n" +
                "\n" +
                hb3.toString() +
                "\n" +
                hb4.toString() +
                "\n" +
                "--" +
                "\n" +
                "Notification Built: " + utcNowFormatted +
                "\n" +
                "--";
        assertEquals(expectedBody2, hostsRegisteredNotification.getMessage());
    }
}
