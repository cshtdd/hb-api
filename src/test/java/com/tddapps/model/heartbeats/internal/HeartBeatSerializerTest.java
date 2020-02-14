package com.tddapps.model.heartbeats.internal;

import com.tddapps.model.heartbeats.HeartBeat;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.text.ParseException;

import static com.tddapps.utils.DateExtensions.EpochSecondsPlusMs;
import static com.tddapps.utils.DateExtensions.ToReverseUtcMinuteString;
import static com.tddapps.utils.EqualityAssertions.shouldBeEqual;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class HeartBeatSerializerTest {
    private final String MAXIMUM_LENGTH_ALLOWED_STRING = StringUtils.leftPad("", 100, "0");
    private final String INVALID_HOST_ID = "Invalid hostId";

    private final HeartBeatSerializer serializer = new HeartBeatSerializer();

    @Test
    void parseUnsanitizedBuildsAHeartBeat() throws ParseException {
        val expected = new HeartBeat(
                "superHost1",
                EpochSecondsPlusMs(40000),
                ToReverseUtcMinuteString(EpochSecondsPlusMs(40000)),
                "",
                false
        );

        shouldBeEqual(expected, serializer.parseUnsanitizedJson("{\"hostId\": \"superHost1\", \"intervalMs\": 40000}"));
    }

    @Test
    void parseUnsanitizedIgnoresTheRegionAndTest() throws ParseException {
        val expected = new HeartBeat(
                "superHost1",
                EpochSecondsPlusMs(40000),
                ToReverseUtcMinuteString(EpochSecondsPlusMs(40000)),
                "",
                false
        );

        shouldBeEqual(expected, serializer.parseUnsanitizedJson(
                "{\"hostId\": \"superHost1\", \"intervalMs\": 40000, \"region\":\"us-east-1\", \"test\":true}"
        ));
    }

    @Test
    void parseUnsanitizedFailsWhenEmptyInput(){
        parseUnsanitizedShouldFailWithError(null, "Empty input");
        parseUnsanitizedShouldFailWithError("", "Empty input");
        parseUnsanitizedShouldFailWithError(" ", "Empty input");
    }

    @Test
    void parseUnsanitizedFailsWhenInvalidJsonInput(){
        parseUnsanitizedShouldFailWithError("fred", "Invalid json");
        parseUnsanitizedShouldFailWithError("{", "Invalid json");
        parseUnsanitizedShouldFailWithError("{\"hostId", "Invalid json");
    }

    @Test
    void parseUnsanitizedReadsTheMaximumLengthHostId() throws ParseException{
        val heartBeat = serializer.parseUnsanitizedJson(String.format(
                "{\"hostId\": \"%s\"}", MAXIMUM_LENGTH_ALLOWED_STRING
        ));

        assertEquals(MAXIMUM_LENGTH_ALLOWED_STRING, heartBeat.getHostId());
    }

    @Test
    void parseUnsanitizedFailsWhenHostIdIsMissing(){
        parseUnsanitizedShouldFailWithError("{}", INVALID_HOST_ID);
        parseUnsanitizedShouldFailWithError("{\"hostId\": \"\"}", INVALID_HOST_ID);
        parseUnsanitizedShouldFailWithError("{\"hostId\": \"   \"}", INVALID_HOST_ID);
    }

    @Test
    void parseUnsanitizedFailsWhenHostIdIsNotAlphanumeric(){
        parseUnsanitizedShouldFailWithError("{\"hostId\": \"-!@#$$%^%^ &^&\"}", INVALID_HOST_ID);
    }

    @Test
    void parseUnsanitizedFailsWhenHostIdIsTooLong(){
        parseUnsanitizedShouldFailWithError(String.format(
                "{\"hostId\": \"X%s\"}", MAXIMUM_LENGTH_ALLOWED_STRING
        ), INVALID_HOST_ID);
    }

    @Test
    void parseUnsanitizedSupportsMultipleDataTypesForIntervalMs() throws ParseException {
        val expected = new HeartBeat(
                "superHost1",
                EpochSecondsPlusMs(3000),
                ToReverseUtcMinuteString(EpochSecondsPlusMs(3000)),
                "",
                false
        );

        shouldBeEqual(expected, serializer.parseUnsanitizedJson("{\"hostId\": \"superHost1\", \"intervalMs\": 3000}"));
        shouldBeEqual(expected, serializer.parseUnsanitizedJson("{\"hostId\": \"superHost1\", \"intervalMs\": 3000.45}"));
        shouldBeEqual(expected, serializer.parseUnsanitizedJson("{\"hostId\": \"superHost1\", \"intervalMs\": \"3000\"}"));
    }

    @Test
    void parseUnsanitizedAssumesDefaultWhenIntervalMsIsNotNumeric() throws ParseException {
        val expected = new HeartBeat(
                "superHost1",
                EpochSecondsPlusMs(HeartBeatSerializer.DEFAULT_INTERVAL_MS),
                ToReverseUtcMinuteString(EpochSecondsPlusMs(HeartBeatSerializer.DEFAULT_INTERVAL_MS)),
                "",
                false
        );

        shouldBeEqual(expected, serializer.parseUnsanitizedJson("{\"hostId\": \"superHost1\", \"intervalMs\": null}"));
        shouldBeEqual(expected, serializer.parseUnsanitizedJson("{\"hostId\": \"superHost1\", \"intervalMs\": \"\"}"));
        shouldBeEqual(expected, serializer.parseUnsanitizedJson("{\"hostId\": \"superHost1\", \"intervalMs\": \" \"}"));
        shouldBeEqual(expected, serializer.parseUnsanitizedJson("{\"hostId\": \"superHost1\", \"intervalMs\": \"pete\"}"));
    }

    @Test
    void parseUnsanitizedFailsWhenIntervalMsIsOutOfBoundaries(){
        val INVALID_INTERVAL_MS = "Invalid intervalMs";

        parseUnsanitizedShouldFailWithError("{\"hostId\": \"host1\", \"intervalMs\": 999}", INVALID_INTERVAL_MS);
        parseUnsanitizedShouldFailWithError("{\"hostId\": \"host1\", \"intervalMs\": \"999\"}", INVALID_INTERVAL_MS);
        parseUnsanitizedShouldFailWithError("{\"hostId\": \"host1\", \"intervalMs\": 43200001}", INVALID_INTERVAL_MS);
        parseUnsanitizedShouldFailWithError("{\"hostId\": \"host1\", \"intervalMs\": \"43200001\"}", INVALID_INTERVAL_MS);
    }

    private void parseUnsanitizedShouldFailWithError(String requestBody, String errorMessage){
        try {
            serializer.parseUnsanitizedJson(requestBody);
            fail("Should have thrown");
        } catch (ParseException e) {
            assertEquals(errorMessage, e.getMessage());
        }
    }

}
