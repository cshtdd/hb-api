package com.tddapps.dal;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class HeartBeatTest {
    @Test
    public void HasSensibleStringRepresentation(){
        ZonedDateTime dateTime = ZonedDateTime.of(2017, 7, 17, 20, 5, 31, 0, ZoneId.of("UTC"));
        Date expirationUtc = Date.from(dateTime.toInstant());

        HeartBeat heartBeat = new HeartBeat("myHost", expirationUtc);

        assertEquals("HeartBeat, expirationUtc: 2017-07-17T20:05:31Z[UTC], hostId: myHost", heartBeat.toString());
    }
}
