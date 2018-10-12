package com.tddapps.model;

import com.tddapps.model.test.HeartBeatFactory;
import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class HeartBeatChangeEventTest {
    @Test
    public void HasSensibleStringRepresentation(){
        val hb = HeartBeatFactory.Create("host1");
        val event = new HeartBeatChangeEvent("creation", hb);

        assertEquals("HeartBeatChangeEvent, type: creation, hostId: host1", event.toString());
    }

    @Test
    public void CanBeCompared(){
        val hb1 = HeartBeatFactory.Create("host1");
        val hb2 = HeartBeatFactory.Create("host2");
        val hb1Copy = new HeartBeat(
                hb1.getHostId(),
                hb1.getTtl(),
                hb1.getRegion(),
                hb1.isTest()
        );

        val event1 = new HeartBeatChangeEvent("creation", hb1);
        val event1Copy = new HeartBeatChangeEvent("creation", hb1);
        val event1CopyDifferentType = new HeartBeatChangeEvent("deletion", hb1);
        val event1EquivalentHb = new HeartBeatChangeEvent("creation", hb1Copy);
        val event2 = new HeartBeatChangeEvent("creation", hb2);

        assertEquals(event1, event1Copy);
        assertEquals(event1, event1EquivalentHb);
        assertNotEquals(event1, event1CopyDifferentType);
        assertNotEquals(event1, event2);
    }
}
