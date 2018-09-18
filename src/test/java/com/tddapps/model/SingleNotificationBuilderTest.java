package com.tddapps.model;

import com.tddapps.utils.UtcNowReader;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static com.tddapps.utils.DateExtensions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SingleNotificationBuilderTest {
    private final UtcNowReader utcNowReaderMock = mock(UtcNowReader.class);
    private final SingleNotificationBuilder builder = new SingleNotificationBuilder(utcNowReaderMock);

    private String utcNowFormatted;

    @BeforeEach
    public void Setup(){
        val seededDate = UtcNowPlusMs(1000);
        utcNowFormatted = ToUtcString(seededDate);

        when(utcNowReaderMock.Read())
                .thenReturn(seededDate);
    }

    @Test
    public void DoesNotSendNotificationWhenNoHeartBeatsAreProvided(){
        assertEquals(0, builder.build(null).length);
        assertEquals(0, builder.build(new HeartBeat[]{}).length);
    }

    @Test
    public void SendsNotificationForASingleHeartBeat(){
        val hb1 = new HeartBeat("host1", UtcNow(), EpochSecondsNow(), false);
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
    public void SendsSingleNotificationForMultipleHeartBeats(){
        val hb1 = new HeartBeat("host1", UtcNow(), EpochSecondsNow(), false);
        val hb2 = new HeartBeat("host2", UtcNow(), EpochSecondsNow(), false);
        val input = new HeartBeat[]{
                hb1,
                hb2
        };

        val notifications = builder.build(input);
        assertEquals(1, notifications.length);
        val notification = notifications[0];

        assertEquals("Hosts missing [host1, host2]", notification.getSubject());
        val expectedBody = "Hosts missing [host1, host2]\n" +
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
        assertEquals(expectedBody, notification.getMessage());
    }
}
