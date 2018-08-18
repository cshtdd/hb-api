package com.tddapps.dal;

import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import static com.tddapps.utils.DateExtensions.UtcNow;
import static com.tddapps.utils.DateExtensions.UtcNowPlusMs;
import static com.tddapps.utils.EqualityAssertions.shouldBeEqual;
import static com.tddapps.utils.EqualityAssertions.shouldNotBeEqual;
import static org.junit.jupiter.api.Assertions.*;

public class HeartBeatTest {
    @Test
    public void HasSensibleStringRepresentation(){
        ZonedDateTime dateTime = ZonedDateTime.of(2017, 7, 17, 20, 5, 31, 0, ZoneId.of("UTC"));
        Date expirationUtc = Date.from(dateTime.toInstant());

        HeartBeat heartBeat = new HeartBeat("myHost", expirationUtc);

        assertEquals("HeartBeat, expirationUtc: 2017-07-17T20:05:31Z[UTC], hostId: myHost, isTest: false", heartBeat.toString());
        assertTrue(heartBeat.isNotTest());
    }

    @Test
    public void HasSensibleStringRepresentationForEmptyObject(){
        assertEquals("HeartBeat, expirationUtc: null, hostId: , isTest: false", new HeartBeat().toString());
    }

    @Test
    public void NoNeedToSpecifyIsTestByDefault(){
        HeartBeat hb = new HeartBeat("host1", UtcNowPlusMs(6000));
        assertFalse(hb.isTest());
        assertTrue(hb.isNotTest());
    }

    @Test
    public void IsTestIsConsideredForEquality(){
        Date date1 = UtcNow();

        HeartBeat hb1 = new HeartBeat("host1", date1);
        HeartBeat hb1Clone = new HeartBeat("host1", date1, false);
        HeartBeat hb1CloneReallyCloseDate = new HeartBeat("host1", UtcNowPlusMs(1), false);
        HeartBeat hb1Test = new HeartBeat("host1", date1, true);
        HeartBeat hb1TestReallyCloseDate = new HeartBeat("host1", UtcNowPlusMs(1), true);

        shouldBeEqual(hb1, hb1Clone);
        shouldNotBeEqual(hb1, hb1Test);

        assertTrue(hb1.almostEquals(hb1CloneReallyCloseDate));
        assertFalse(hb1.almostEquals(hb1TestReallyCloseDate));
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

    @Test
    public void HeartBeatsWithCloseExpirationAreNotExpired(){
        assertFalse(new HeartBeat("", UtcNowPlusMs(50)).isExpired());
        assertTrue(new HeartBeat("", UtcNowPlusMs(50)).isNotExpired());
        assertFalse(new HeartBeat("", UtcNowPlusMs(-50)).isExpired());
        assertTrue(new HeartBeat("", UtcNowPlusMs(-50)).isNotExpired());
    }

    @Test
    public void FutureHeartBeatsAreNotExpired(){
        assertFalse(new HeartBeat("", UtcNowPlusMs(5000)).isExpired());
        assertTrue(new HeartBeat("", UtcNowPlusMs(5000)).isNotExpired());
    }

    @Test
    public void PastHeartBeatsAreExpired(){
        assertTrue(new HeartBeat("", UtcNowPlusMs(-5000)).isExpired());
        assertFalse(new HeartBeat("", UtcNowPlusMs(-5000)).isNotExpired());
    }

    @Test
    public void HeartBeatsWithNoExpirationAreExpired(){
        assertTrue(new HeartBeat("", null).isExpired());
        assertFalse(new HeartBeat("", null).isNotExpired());
    }
}
