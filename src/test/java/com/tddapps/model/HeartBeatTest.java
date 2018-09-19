package com.tddapps.model;

import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.text.ParseException;

import static com.tddapps.utils.DateExtensions.*;
import static com.tddapps.utils.EqualityAssertions.shouldBeEqual;
import static com.tddapps.utils.EqualityAssertions.shouldNotBeEqual;
import static org.junit.jupiter.api.Assertions.*;

public class HeartBeatTest {
    private final String MAXIMUM_LENGTH_ALLOWED_STRING = StringUtils.leftPad("", 100, "0");
    private final String INVALID_HOST_ID = "Invalid hostId";
    private final String INVALID_INTERVAL_MS = "Invalid intervalMs";

    @Test
    public void HasSensibleStringRepresentation(){
        val seededTtl = EpochSecondsPlusMs(5000);
        val expectedExpirationString = ToUtcString(seededTtl);

        val heartBeat = new HeartBeat("myHost", seededTtl, false);

        val expected = String.format(
                "HeartBeat, expirationUtc: %s, hostId: myHost, ttl: %d, isTest: false",
                expectedExpirationString,
                seededTtl
        );
        assertEquals(expected, heartBeat.toString());
        assertFalse(heartBeat.isTest());
        assertTrue(heartBeat.isNotTest());
    }

    @Test
    public void HasSensibleStringRepresentationForEmptyObject(){
        assertEquals("HeartBeat, expirationUtc: 1970-01-01T00:00:00Z[UTC], hostId: , ttl: 0, isTest: false", new HeartBeat().toString());
    }

    @Test
    public void IsTestIsConsideredForEquality(){
        val ttl1 = EpochSecondsNow();

        val hb1 = new HeartBeat("host1", ttl1, false);
        val hb1Clone = new HeartBeat("host1", ttl1, false);
        val hb1CloneReallyCloseDate = new HeartBeat("host1", EpochSecondsPlusMs(1000), false);
        val hb1Test = new HeartBeat("host1", ttl1, true);
        val hb1TestReallyCloseDate = new HeartBeat("host1", EpochSecondsPlusMs(1000), true);

        shouldBeEqual(hb1, hb1Clone);
        shouldNotBeEqual(hb1, hb1Test);

        shouldNotBeEqual(hb1, hb1CloneReallyCloseDate);
        shouldNotBeEqual(hb1, hb1TestReallyCloseDate);
    }

    @Test
    public void CanBeCompared(){
        val ttl1 = EpochSecondsNow();

        val hbNoHost = new HeartBeat(null, ttl1, false);
        val hbEmptyHost = new HeartBeat("", ttl1, false);
        val hb1 = new HeartBeat("host1", ttl1,false);
        val hb1Copy = new HeartBeat("host1", ttl1,false);
        val hb1ReallyCloseDate = new HeartBeat("host1", EpochSecondsPlusMs(1000), false);
        val hb1DifferentDate = new HeartBeat("host1", EpochSecondsPlusMs(3000), false);
        val hb2 = new HeartBeat("host2", ttl1, false);

        shouldBeEqual(hb1, hb1Copy);

        shouldNotBeEqual(hbEmptyHost, hbNoHost);
        shouldNotBeEqual(hb1, null);
        shouldNotBeEqual(hb1, 45);
        shouldNotBeEqual(hb1, hbNoHost);
        shouldNotBeEqual(hb1, hb2);
        shouldNotBeEqual(hb1, hb1DifferentDate);
        shouldNotBeEqual(hb1, hb1ReallyCloseDate);
    }

    @Test
    public void CanBeCloned(){
        val hb = new HeartBeat("host1", EpochSecondsPlusMs(3000), false);
        val hbClone = (HeartBeat) hb.clone();

        assertFalse(hb == hbClone);
        assertEquals(hb, hbClone);

        assertEquals(hb.getHostId(), hbClone.getHostId());
        assertEquals(hb.getTtl(), hbClone.getTtl());
        assertEquals(hb.isTest(), hbClone.isTest());

        hb.setHostId("different");
        hb.setTtl(EpochSecondsPlusMs(100000));
        hb.setTest(true);

        assertNotEquals(hb.getHostId(), hbClone.getHostId());
        assertNotEquals(hb.getTtl(), hbClone.getTtl());
        assertNotEquals(hb.isTest(), hbClone.isTest());
    }

    @Test
    public void CloneWithUpdatedTtlReturnsACopyOfTheOriginalHeartBeatWithADifferentTtl(){
        val expectedHeartBeat = new HeartBeat("host1", EpochSecondsPlusMs(3000), false);

        val actualHeartBeat = new HeartBeat("host1", EpochSecondsNow(), false)
                .clone(EpochSecondsPlusMs(3000));

        assertEquals(expectedHeartBeat, actualHeartBeat);
    }

    @Test
    public void ParseJsonBuildsAHeartBeat() throws ParseException {
        val expected = new HeartBeat(
                "superHost1",
                EpochSecondsPlusMs(40000),
                false
        );

        shouldBeEqual(expected, HeartBeat.parse("{\"hostId\": \"superHost1\", \"intervalMs\": 40000}"));
    }

    @Test
    public void ParseJsonFailsWhenEmptyInput(){
        parseJsonShouldFailWithError(null, "Empty input");
        parseJsonShouldFailWithError("", "Empty input");
        parseJsonShouldFailWithError(" ", "Empty input");
    }

    @Test
    public void ParseJsonFailsWhenInvalidJsonInput(){
        parseJsonShouldFailWithError("fred", "Invalid json");
        parseJsonShouldFailWithError("{", "Invalid json");
        parseJsonShouldFailWithError("{\"hostId", "Invalid json");
    }

    @Test
    public void ParseJsonReadsTheMaximumLengthHostId() throws ParseException{
        val heartBeat = HeartBeat.parse(String.format(
                "{\"hostId\": \"%s\"}", MAXIMUM_LENGTH_ALLOWED_STRING
        ));

        assertEquals(MAXIMUM_LENGTH_ALLOWED_STRING, heartBeat.getHostId());
    }

    @Test
    public void ParseJsonFailsWhenHostIdIsMissing(){
        parseJsonShouldFailWithError("{}", INVALID_HOST_ID);
        parseJsonShouldFailWithError("{\"hostId\": \"\"}", INVALID_HOST_ID);
        parseJsonShouldFailWithError("{\"hostId\": \"   \"}", INVALID_HOST_ID);
    }

    @Test
    public void ParseJsonFailsWhenHostIdIsNotAlphanumeric(){
        parseJsonShouldFailWithError("{\"hostId\": \"-!@#$$%^%^ &^&\"}", INVALID_HOST_ID);
    }

    @Test
    public void ParseJsonFailsWhenHostIdIsTooLong(){
        parseJsonShouldFailWithError(String.format(
                "{\"hostId\": \"X%s\"}", MAXIMUM_LENGTH_ALLOWED_STRING
        ), INVALID_HOST_ID);
    }

    @Test
    public void ParseJsonSupportsMultipleDataTypesForIntervalMs() throws ParseException {
        val expected = new HeartBeat(
                "superHost1",
                EpochSecondsPlusMs(3000),
                false
        );

        shouldBeEqual(expected, HeartBeat.parse("{\"hostId\": \"superHost1\", \"intervalMs\": 3000}"));
        shouldBeEqual(expected, HeartBeat.parse("{\"hostId\": \"superHost1\", \"intervalMs\": 3000.45}"));
        shouldBeEqual(expected, HeartBeat.parse("{\"hostId\": \"superHost1\", \"intervalMs\": \"3000\"}"));
    }

    @Test
    public void ParseJsonAssumesDefaultWhenIntervalMsIsNotNumeric() throws ParseException {
        val expected = new HeartBeat(
                "superHost1",
                EpochSecondsPlusMs(HeartBeat.DEFAULT_INTERVAL_MS),
                false
        );

        shouldBeEqual(expected, HeartBeat.parse("{\"hostId\": \"superHost1\", \"intervalMs\": null}"));
        shouldBeEqual(expected, HeartBeat.parse("{\"hostId\": \"superHost1\", \"intervalMs\": \"\"}"));
        shouldBeEqual(expected, HeartBeat.parse("{\"hostId\": \"superHost1\", \"intervalMs\": \" \"}"));
        shouldBeEqual(expected, HeartBeat.parse("{\"hostId\": \"superHost1\", \"intervalMs\": \"pete\"}"));
    }

    @Test
    public void ParseJsonFailsWhenIntervalMsIsOutOfBoundaries(){
        parseJsonShouldFailWithError("{\"hostId\": \"host1\", \"intervalMs\": 999}", INVALID_INTERVAL_MS);
        parseJsonShouldFailWithError("{\"hostId\": \"host1\", \"intervalMs\": \"999\"}", INVALID_INTERVAL_MS);
        parseJsonShouldFailWithError("{\"hostId\": \"host1\", \"intervalMs\": 43200001}", INVALID_INTERVAL_MS);
        parseJsonShouldFailWithError("{\"hostId\": \"host1\", \"intervalMs\": \"43200001\"}", INVALID_INTERVAL_MS);
    }

    private void parseJsonShouldFailWithError(String requestBody, String errorMessage){
        try {
            HeartBeat.parse(requestBody);
            fail("Should have thrown");
        } catch (ParseException e) {
            assertEquals(errorMessage, e.getMessage());
        }
    }
}
