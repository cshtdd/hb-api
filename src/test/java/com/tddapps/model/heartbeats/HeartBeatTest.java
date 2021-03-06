package com.tddapps.model.heartbeats;

import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.text.ParseException;

import static com.tddapps.model.heartbeats.test.HeartBeatFactory.TEST_REGION_DEFAULT;
import static com.tddapps.utils.DateExtensions.*;
import static com.tddapps.utils.EqualityAssertions.shouldBeEqual;
import static com.tddapps.utils.EqualityAssertions.shouldNotBeEqual;
import static org.junit.jupiter.api.Assertions.*;

class HeartBeatTest {
    @Test
    void HasSensibleStringRepresentation(){
        val seededTtl = EpochSecondsPlusMs(5000);
        val expectedExpirationString = ToUtcString(seededTtl);

        val heartBeat = new HeartBeat("myHost", seededTtl, "AAAA", "ap-east-1", false);

        val expected = String.format(
                "HeartBeat, expirationUtc: %s, hostId: myHost, ttl: %d, expirationMinuteUtc: AAAA, region: ap-east-1, isTest: false, isExpired: false",
                expectedExpirationString,
                seededTtl
        );
        assertEquals(expected, heartBeat.toString());
        assertFalse(heartBeat.isTest());
        assertTrue(heartBeat.isNotTest());
    }

    @Test
    void HeartBeatsAreExpiredWhenTtlIsInThePast(){
        long ttlInThePast = EpochSecondsPlusMs(-1001);
        val heartBeat = new HeartBeat("host1", ttlInThePast, "AAAA", "region1",false);

        assertTrue(heartBeat.isExpired());
        assertFalse(heartBeat.isNotExpired());
    }

    @Test
    void HeartBeatsAreNotExpiredWhenTtlIsInTheFuture(){
        long ttlInTheFuture = EpochSecondsPlusMs(1001);
        val heartBeat = new HeartBeat("host1", ttlInTheFuture, "AAAA", "region1",false);

        assertFalse(heartBeat.isExpired());
        assertTrue(heartBeat.isNotExpired());
    }

    @Test
    void SecondsUntilExpirationIsOneMoreThanTheExpirationTime(){
        long ttl = EpochSecondsPlusMs(1000);
        val heartBeat = new HeartBeat("host1", ttl, "AAAA", "region1",false);

        assertEquals(2, heartBeat.secondsUntilExpiration());
    }

    @Test
    void SecondsUntilExpirationIsZeroWhenHeartBeatExpired(){
        long ttl = EpochSecondsPlusMs(-3000);
        val heartBeat = new HeartBeat("host1", ttl, "AAAA", "region1",false);

        assertEquals(0, heartBeat.secondsUntilExpiration());
    }

    @Test
    void SecondsUntilExpirationCannotBeMoreThanTheMaximumAllowedSQSDelay(){
        long ttl = EpochSecondsPlusMs(901000);
        val heartBeat = new HeartBeat("host1", ttl, "AAAA", "region1",false);

        assertEquals(900, heartBeat.secondsUntilExpiration());
    }

    @Test
    void HasSensibleStringRepresentationForEmptyObject(){
        assertEquals(
                "HeartBeat, expirationUtc: 1970-01-01T00:00:00Z[UTC], hostId: , ttl: 0, expirationMinuteUtc: , region: , isTest: false, isExpired: true",
                new HeartBeat().toString());
    }

    @Test
    void CanBeConstructedWithoutSpecifyingTheExpirationMinuteUtc(){
        long ttlNow = EpochSecondsNow();

        val hb1 = new HeartBeat("host1", ttlNow, ToReverseUtcMinuteString(ttlNow), TEST_REGION_DEFAULT, true);
        val hb2 = new HeartBeat("host1", ttlNow, TEST_REGION_DEFAULT, true);

        assertEquals(hb1, hb2);
    }

    @Test
    void IsTestIsConsideredForEquality(){
        val ttl1 = EpochSecondsNow();

        val hb1 = new HeartBeat("host1", ttl1, "AAAA", "us-west-1", false);
        val hb1Clone = new HeartBeat("host1", ttl1, "AAAA", "us-west-1", false);
        val hb1CloneReallyCloseDate = new HeartBeat("host1", EpochSecondsPlusMs(1000), "BBBB", "us-west-1", false);
        val hb1Test = new HeartBeat("host1", ttl1, "AAAA", "us-west-1", true);
        val hb1TestReallyCloseDate = new HeartBeat("host1", EpochSecondsPlusMs(1000), "BBBB", "us-west-1", true);

        shouldBeEqual(hb1, hb1Clone);
        shouldNotBeEqual(hb1, hb1Test);

        shouldNotBeEqual(hb1, hb1CloneReallyCloseDate);
        shouldNotBeEqual(hb1, hb1TestReallyCloseDate);
    }

    @Test
    void CanBeCompared(){
        val ttl1 = EpochSecondsNow();

        val hbNoHost = new HeartBeat(null, ttl1, "AAAA", "us-west-1", false);
        val hbEmptyHost = new HeartBeat("", ttl1, "AAAA", "us-west-1", false);
        val hb1 = new HeartBeat("host1", ttl1, "AAAA", "us-west-1", false);
        val hb1Copy = new HeartBeat("host1", ttl1, "AAAA", "us-west-1", false);
        val hb1DifferentRegion = new HeartBeat("host1", ttl1, "AAAA", "ap-east-2", false);
        val hb1DifferentExpirationMinuteUtc = new HeartBeat("host1", ttl1, "BBBB", "us-west-1", false);
        val hb1ReallyCloseDate = new HeartBeat("host1", EpochSecondsPlusMs(1000), "AAAA", "us-west-1", false);
        val hb1DifferentDate = new HeartBeat("host1", EpochSecondsPlusMs(3000), "AAAA", "us-west-1", false);
        val hb2 = new HeartBeat("host2", ttl1, "AAAA","us-west-1", false);

        shouldBeEqual(hb1, hb1Copy);

        shouldNotBeEqual(hbEmptyHost, hbNoHost);
        shouldNotBeEqual(hb1, null);
        shouldNotBeEqual(hb1, 45);
        shouldNotBeEqual(hb1, hbNoHost);
        shouldNotBeEqual(hb1, hb2);
        shouldNotBeEqual(hb1, hb1DifferentExpirationMinuteUtc);
        shouldNotBeEqual(hb1, hb1DifferentRegion);
        shouldNotBeEqual(hb1, hb1DifferentDate);
        shouldNotBeEqual(hb1, hb1ReallyCloseDate);
    }
}
