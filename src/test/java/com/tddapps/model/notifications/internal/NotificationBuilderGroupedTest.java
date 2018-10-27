package com.tddapps.model.notifications.internal;

import com.tddapps.model.heartbeats.HeartBeat;
import com.tddapps.model.heartbeats.HeartBeatChangeEvent;
import com.tddapps.model.heartbeats.test.HeartBeatFactory;
import com.tddapps.model.notifications.HeartBeatNotificationBuilder;
import com.tddapps.model.notifications.test.HeartBeatNotificationBuilderOneToOneStub;
import lombok.val;
import org.junit.jupiter.api.Test;

import static com.tddapps.model.heartbeats.test.HeartBeatFactory.TEST_REGION_DEFAULT;
import static com.tddapps.utils.DateExtensions.EpochSecondsNow;
import static com.tddapps.utils.DateExtensions.ToReverseUtcMinuteString;
import static org.junit.jupiter.api.Assertions.*;

class NotificationBuilderGroupedTest {
    private final HeartBeatNotificationBuilder notificationBuilderMock = new HeartBeatNotificationBuilderOneToOneStub();
    private final NotificationBuilderGrouped builder = new NotificationBuilderGrouped(notificationBuilderMock);

    @Test
    void DoesNotAllowANullInput(){
        try{
            builder.build(null);
            fail("Should have thrown");
        }catch (NullPointerException e){
            assertNotNull(e);
        }
    }

    @Test
    void DoesNotSendNotificationWhenNoHeartBeatsAreProvided(){
        assertEquals(0, builder.build(new HeartBeatChangeEvent[]{}).length);
    }

    @Test
    void SendsNotificationForASingleEvent() {
        val hb1 = new HeartBeat("host1", EpochSecondsNow(), ToReverseUtcMinuteString(EpochSecondsNow()), TEST_REGION_DEFAULT, false);
        val event1 = new HeartBeatChangeEvent("deleted", hb1);

        val notifications = builder.build(new HeartBeatChangeEvent[]{event1});

        assertEquals(1, notifications.length);
        assertEquals("SS-host1", notifications[0].getSubject());
        assertEquals("MM-host1-deleted", notifications[0].getMessage());
    }

    @Test
    void GroupsNotificationsByEventType(){
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
