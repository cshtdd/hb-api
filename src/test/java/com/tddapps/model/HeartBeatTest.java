package com.tddapps.model;

import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

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
        val heartBeat = new HeartBeat("myHost", 10000000, false);

        assertEquals("HeartBeat, hostId: myHost, ttl: 10000000, isTest: false", heartBeat.toString());
        assertFalse(heartBeat.isTest());
        assertTrue(heartBeat.isNotTest());
    }

    @Test
    public void HasSensibleStringRepresentationForEmptyObject(){
        assertEquals("HeartBeat, hostId: , ttl: 0, isTest: false", new HeartBeat().toString());
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
    public void ParsingBuildsAHeartBeat() throws ParseException {
        val expected = new HeartBeat(
                "superHost1",
                EpochSecondsPlusMs(40000),
                false
        );

        shouldBeEqual(expected, HeartBeat.parse("{\"hostId\": \"superHost1\", \"intervalMs\": 40000}"));
    }

    @Test
    public void ParsingFailsWhenEmptyInput(){
        parseShouldFailWithError(null, "Empty input");
        parseShouldFailWithError("", "Empty input");
        parseShouldFailWithError(" ", "Empty input");
    }

    @Test
    public void ParsingFailsWhenInvalidJsonInput(){
        parseShouldFailWithError("fred", "Invalid json");
        parseShouldFailWithError("{", "Invalid json");
        parseShouldFailWithError("{\"hostId", "Invalid json");
    }

    @Test
    public void ReadsTheMaximumLengthHostId() throws ParseException{
        val heartBeat = HeartBeat.parse(String.format(
                "{\"hostId\": \"%s\"}", MAXIMUM_LENGTH_ALLOWED_STRING
        ));

        assertEquals(MAXIMUM_LENGTH_ALLOWED_STRING, heartBeat.getHostId());
    }

    @Test
    public void ParsingFailsWhenHostIdIsMissing(){
        parseShouldFailWithError("{}", INVALID_HOST_ID);
        parseShouldFailWithError("{\"hostId\": \"\"}", INVALID_HOST_ID);
        parseShouldFailWithError("{\"hostId\": \"   \"}", INVALID_HOST_ID);
    }

    @Test
    public void ParsingFailsWhenHostIdIsNotAlphanumeric(){
        parseShouldFailWithError("{\"hostId\": \"-!@#$$%^%^ &^&\"}", INVALID_HOST_ID);
    }

    @Test
    public void ParsingFailsWhenHostIdIsTooLong(){
        parseShouldFailWithError(String.format(
                "{\"hostId\": \"X%s\"}", MAXIMUM_LENGTH_ALLOWED_STRING
        ), INVALID_HOST_ID);
    }

    @Test
    public void ParsingSupportsMultipleDataTypesForIntervalMs() throws ParseException {
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
    public void ParsingAssumesDefaultWhenIntervalMsIsNotNumeric() throws ParseException {
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
    public void ParsingFailsWhenIntervalMsIsOutOfBoundaries(){
        parseShouldFailWithError("{\"hostId\": \"host1\", \"intervalMs\": 999}", INVALID_INTERVAL_MS);
        parseShouldFailWithError("{\"hostId\": \"host1\", \"intervalMs\": \"999\"}", INVALID_INTERVAL_MS);
        parseShouldFailWithError("{\"hostId\": \"host1\", \"intervalMs\": 43200001}", INVALID_INTERVAL_MS);
        parseShouldFailWithError("{\"hostId\": \"host1\", \"intervalMs\": \"43200001\"}", INVALID_INTERVAL_MS);
    }

    private void parseShouldFailWithError(String requestBody, String errorMessage){
        try {
            HeartBeat.parse(requestBody);
            fail("Should have thrown");
        } catch (ParseException e) {
            assertEquals(errorMessage, e.getMessage());
        }
    }
}
