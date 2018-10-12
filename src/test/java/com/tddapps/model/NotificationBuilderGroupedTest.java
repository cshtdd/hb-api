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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NotificationBuilderGroupedTest {
    @Deprecated
    private final NowReader nowReaderMock = mock(NowReader.class);
    private final HeartBeatNotificationBuilder notificationBuilderMock = new HeartBeatNotificationBuilderOneToOneStub();
    private final NotificationBuilderGrouped builder = new NotificationBuilderGrouped(nowReaderMock, notificationBuilderMock);

    private String utcNowFormatted;

    @Deprecated
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
    public void SendsNotificationForASingleEvent2() {
        val hb1 = new HeartBeat("host1", EpochSecondsNow(), ToReverseUtcMinuteString(EpochSecondsNow()), TEST_REGION_DEFAULT, false);
        val event1 = new HeartBeatChangeEvent("deleted", hb1);

        val notifications = builder.build(new HeartBeatChangeEvent[]{event1});

        assertEquals(1, notifications.length);
        assertEquals("SS-host1", notifications[0].getSubject());
        assertEquals("MM-host1-deleted", notifications[0].getMessage());
    }

    @Test
    public void GroupsNotificationsByEventType2(){
        val input = new HeartBeatChangeEvent[]{
                new HeartBeatChangeEvent("deleted", HeartBeatFactory.Create("host1")),
                new HeartBeatChangeEvent("deleted", HeartBeatFactory.Create("host2")),
                new HeartBeatChangeEvent("created", HeartBeatFactory.Create("host3")),
                new HeartBeatChangeEvent("created", HeartBeatFactory.Create("host4"))
        };

        val notifications = builder.build(input);

        assertEquals(4, notifications.length);

        assertEquals("SS-host1", notifications[0].getSubject());
        assertEquals("MM-host1-deleted", notifications[0].getMessage());
        assertEquals("SS-host2", notifications[1].getSubject());
        assertEquals("MM-host2-deleted", notifications[1].getMessage());

        assertEquals("SS-host3", notifications[2].getSubject());
        assertEquals("MM-host3-created", notifications[2].getMessage());
        assertEquals("SS-host4", notifications[3].getSubject());
        assertEquals("MM-host4-created", notifications[3].getMessage());
    }
}
