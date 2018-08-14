package com.tddapps.dal;

import org.junit.jupiter.api.Test;

import static com.tddapps.utils.DateExtensions.UtcNow;
import static com.tddapps.utils.DateExtensions.UtcNowPlusMs;
import static com.tddapps.utils.EqualityAssertions.shouldBeEqual;
import static com.tddapps.utils.EqualityAssertions.shouldNotBeEqual;
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

    @Test
    public void HasSensibleStringRepresentationForEmptyObject(){
        assertEquals("HeartBeat, expirationUtc: null, hostId: ", new HeartBeat().toString());
    }

    @Test
    public void CanBeCompared(){
        Date date1 = UtcNow();

        HeartBeat hbNoHost = new HeartBeat(null, date1);
        HeartBeat hbEmptyHost = new HeartBeat("", date1);
        HeartBeat hbNoDate = new HeartBeat("host1", null);
        HeartBeat hb1 = new HeartBeat("host1", date1);
        HeartBeat hb1Copy = new HeartBeat("host1", date1);
        HeartBeat hb1ReallyCloseDate = new HeartBeat("host1", UtcNowPlusMs(1));
        HeartBeat hb1DifferentDate = new HeartBeat("host1", UtcNowPlusMs(3000));
        HeartBeat hb2 = new HeartBeat("host2", date1);

        shouldBeEqual(hb1, hb1Copy);
        shouldBeEqual(hbEmptyHost, hbNoHost);

        shouldNotBeEqual(hb1, null);
        shouldNotBeEqual(hb1, 45);
        shouldNotBeEqual(hb1, hbNoHost);
        shouldNotBeEqual(hb1, hbNoDate);
        shouldNotBeEqual(hb1, hb2);
        shouldNotBeEqual(hb1, hb1DifferentDate);
        shouldNotBeEqual(hb1, hb1ReallyCloseDate);
    }

    @Test
    public void HasAnAlmostEqualMethod(){
        Date date1 = UtcNow();

        HeartBeat hbNoHost = new HeartBeat(null, date1);
        HeartBeat hbEmptyHost = new HeartBeat("", date1);
        HeartBeat hbNoDate = new HeartBeat("host1", null);
        HeartBeat hb1 = new HeartBeat("host1", date1);
        HeartBeat hb1Copy = new HeartBeat("host1", date1);
        HeartBeat hb1ReallyCloseDate = new HeartBeat("host1", UtcNowPlusMs(1));
        HeartBeat hb1DifferentDate = new HeartBeat("host1", UtcNowPlusMs(3000));
        HeartBeat hb2 = new HeartBeat("host2", date1);

        assertTrue(hb1.almostEquals(hb1Copy));
        assertTrue(hbEmptyHost.almostEquals(hbNoHost));
        assertTrue(hb1.almostEquals(hb1ReallyCloseDate));

        assertFalse(hb1.almostEquals(null));
        assertFalse(hb1.almostEquals(hbNoHost));
        assertFalse(hb1.almostEquals(hbNoDate));
        assertFalse(hb1.almostEquals(hb2));
        assertFalse(hb1.almostEquals(hb1DifferentDate));
    }
}
