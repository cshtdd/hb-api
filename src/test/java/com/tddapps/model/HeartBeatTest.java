package com.tddapps.model;

import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import static com.tddapps.utils.DateExtensions.AreAlmostEquals;
import static com.tddapps.utils.DateExtensions.UtcNow;
import static com.tddapps.utils.DateExtensions.UtcNowPlusMs;
import static com.tddapps.utils.EqualityAssertions.shouldBeEqual;
import static com.tddapps.utils.EqualityAssertions.shouldNotBeEqual;
import static org.junit.jupiter.api.Assertions.*;

public class HeartBeatTest {
    private final String MAXIMUM_LENGTH_ALLOWED_STRING = StringUtils.leftPad("", 100, "0");
    private final String INVALID_HOST_ID = "Invalid hostId";
    private final String INVALID_INTERVAL_MS = "Invalid intervalMs";

    @Test
    public void HasSensibleStringRepresentation(){
        val dateTime = ZonedDateTime.of(2017, 7, 17, 20, 5, 31, 0, ZoneId.of("UTC"));
        val expirationUtc = Date.from(dateTime.toInstant());

        val heartBeat = new HeartBeat("myHost", expirationUtc, false);

        assertEquals("HeartBeat, expirationUtc: 2017-07-17T20:05:31Z[UTC], hostId: myHost, isTest: false", heartBeat.toString());
        assertTrue(heartBeat.isNotTest());
    }

    @Test
    public void HasSensibleStringRepresentationForEmptyObject(){
        assertEquals("HeartBeat, expirationUtc: null, hostId: , isTest: false", new HeartBeat().toString());
    }

    @Test
    public void NoNeedToSpecifyIsTestByDefault(){
        val hb = new HeartBeat("host1", UtcNowPlusMs(6000), false);

        assertFalse(hb.isTest());
        assertTrue(hb.isNotTest());
    }

    @Test
    public void IsTestIsConsideredForEquality(){
        val date1 = UtcNow();

        val hb1 = new HeartBeat("host1", date1, false);
        val hb1Clone = new HeartBeat("host1", date1, false);
        val hb1CloneReallyCloseDate = new HeartBeat("host1", UtcNowPlusMs(1), false);
        val hb1Test = new HeartBeat("host1", date1, true);
        val hb1TestReallyCloseDate = new HeartBeat("host1", UtcNowPlusMs(1), true);

        shouldBeEqual(hb1, hb1Clone);
        shouldNotBeEqual(hb1, hb1Test);

        assertTrue(hb1.almostEquals(hb1CloneReallyCloseDate));
        assertFalse(hb1.almostEquals(hb1TestReallyCloseDate));
    }

    @Test
    public void CanBeCompared(){
        val date1 = UtcNow();

        val hbNoHost = new HeartBeat(null, date1, false);
        val hbEmptyHost = new HeartBeat("", date1, false);
        val hbNoDate = new HeartBeat("host1", null, false);
        val hb1 = new HeartBeat("host1", date1, false);
        val hb1Copy = new HeartBeat("host1", date1, false);
        val hb1ReallyCloseDate = new HeartBeat("host1", UtcNowPlusMs(1), false);
        val hb1DifferentDate = new HeartBeat("host1", UtcNowPlusMs(3000), false);
        val hb2 = new HeartBeat("host2", date1, false);

        shouldBeEqual(hb1, hb1Copy);

        shouldNotBeEqual(hbEmptyHost, hbNoHost);
        shouldNotBeEqual(hb1, null);
        shouldNotBeEqual(hb1, 45);
        shouldNotBeEqual(hb1, hbNoHost);
        shouldNotBeEqual(hb1, hbNoDate);
        shouldNotBeEqual(hb1, hb2);
        shouldNotBeEqual(hb1, hb1DifferentDate);
        shouldNotBeEqual(hb1, hb1ReallyCloseDate);
    }

    @Test
    public void CanBeCloned(){
        val hb = new HeartBeat("host1", UtcNowPlusMs(3000), false);
        val hbClone = (HeartBeat) hb.clone();

        assertFalse(hb == hbClone);
        assertTrue(hb.almostEquals(hbClone));
        assertEquals(hb, hbClone);

        assertEquals(hb.getHostId(), hbClone.getHostId());
        assertTrue(AreAlmostEquals(hb.getExpirationUtc(), hbClone.getExpirationUtc()));
        assertEquals(hb.isTest(), hbClone.isTest());

        hb.setHostId("different");
        hb.setExpirationUtc(UtcNowPlusMs(100000));
        hb.setTest(true);

        assertNotEquals(hb.getHostId(), hbClone.getHostId());
        assertFalse(AreAlmostEquals(hb.getExpirationUtc(), hbClone.getExpirationUtc()));
        assertNotEquals(hb.isTest(), hbClone.isTest());
    }

    @Test
    public void HasAnAlmostEqualMethod(){
        val date1 = UtcNow();

        val hbNoHost = new HeartBeat(null, date1, false);
        val hbEmptyHost = new HeartBeat("", date1, false);
        val hbNoDate = new HeartBeat("host1", null, false);
        val hb1 = new HeartBeat("host1", date1, false);
        val hb1Copy = new HeartBeat("host1", date1, false);
        val hb1ReallyCloseDate = new HeartBeat("host1", UtcNowPlusMs(1), false);
        val hb1DifferentDate = new HeartBeat("host1", UtcNowPlusMs(3000), false);
        val hb2 = new HeartBeat("host2", date1, false);

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
        assertFalse(new HeartBeat("", UtcNowPlusMs(50), false).isExpired());
        assertTrue(new HeartBeat("", UtcNowPlusMs(50), false).isNotExpired());
        assertFalse(new HeartBeat("", UtcNowPlusMs(-50), false).isExpired());
        assertTrue(new HeartBeat("", UtcNowPlusMs(-50), false).isNotExpired());
    }

    @Test
    public void FutureHeartBeatsAreNotExpired(){
        assertFalse(new HeartBeat("", UtcNowPlusMs(5000), false).isExpired());
        assertTrue(new HeartBeat("", UtcNowPlusMs(5000), false).isNotExpired());
    }

    @Test
    public void PastHeartBeatsAreExpired(){
        assertTrue(new HeartBeat("", UtcNowPlusMs(-5000), false).isExpired());
        assertFalse(new HeartBeat("", UtcNowPlusMs(-5000), false).isNotExpired());
    }

    @Test
    public void HeartBeatsWithNoExpirationAreExpired(){
        assertTrue(new HeartBeat("", null, false).isExpired());
        assertFalse(new HeartBeat("", null, false).isNotExpired());
    }

    @Test
    public void CloneWithUpdatedExpirationUtcReturnsACopyOfTheOriginalHeartBeatWithADifferentExpiration(){
        val expectedHeartBeat = new HeartBeat("host1", UtcNowPlusMs(3000), false);

        val actualHeartBeat = new HeartBeat("host1", UtcNow(), false)
                .clone(UtcNowPlusMs(3000));

        assertTrue(expectedHeartBeat.almostEquals(actualHeartBeat));
    }

    @Test
    public void ParsingBuildsAHeartBeat() throws HeartBeatParseException{
        val expected = new HeartBeat(
                "superHost1",
                UtcNowPlusMs(40000),
                false
        );

        assertTrue(expected.almostEquals(HeartBeat.parse("{\"hostId\": \"superHost1\", \"intervalMs\": 40000}")));
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
    public void ReadsTheMaximumLengthHostId() throws HeartBeatParseException{
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
    public void ParsingSupportsMultipleDataTypesForIntervalMs() throws HeartBeatParseException {
        val expected = new HeartBeat(
                "superHost1",
                UtcNowPlusMs(3000),
                false
        );

        assertTrue(expected.almostEquals(HeartBeat.parse("{\"hostId\": \"superHost1\", \"intervalMs\": 3000}")));
        assertTrue(expected.almostEquals(HeartBeat.parse("{\"hostId\": \"superHost1\", \"intervalMs\": 3000.45}")));
        assertTrue(expected.almostEquals(HeartBeat.parse("{\"hostId\": \"superHost1\", \"intervalMs\": \"3000\"}")));
    }

    @Test
    public void ParsingAssumesDefaultWhenIntervalMsIsNotNumeric() throws HeartBeatParseException {
        val expected = new HeartBeat(
                "superHost1",
                UtcNowPlusMs(HeartBeat.DEFAULT_INTERVAL_MS),
                false
        );

        assertTrue(expected.almostEquals(HeartBeat.parse("{\"hostId\": \"superHost1\", \"intervalMs\": null}")));
        assertTrue(expected.almostEquals(HeartBeat.parse("{\"hostId\": \"superHost1\", \"intervalMs\": \"\"}")));
        assertTrue(expected.almostEquals(HeartBeat.parse("{\"hostId\": \"superHost1\", \"intervalMs\": \" \"}")));
        assertTrue(expected.almostEquals(HeartBeat.parse("{\"hostId\": \"superHost1\", \"intervalMs\": \"pete\"}")));
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
        } catch (HeartBeatParseException e) {
            assertEquals(errorMessage, e.getMessage());
        }
    }

}
