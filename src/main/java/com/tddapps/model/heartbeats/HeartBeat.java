package com.tddapps.model.heartbeats;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.fasterxml.jackson.databind.JsonNode;
import com.tddapps.utils.JsonNodeHelper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.ParseException;

import static com.tddapps.utils.DateExtensions.*;
import static com.tddapps.utils.JsonNodeHelper.readInt;
import static com.tddapps.utils.JsonNodeHelper.readString;
import static com.tddapps.utils.StringExtensions.EmptyWhenNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBTable(tableName = "heartbeats")
@Log4j2
public class HeartBeat{
    public static final int MIN_INTERVAL_MS = 1000;
    public static final int MAX_INTERVAL_MS = 12*60*60*1000;
    public static final int DEFAULT_INTERVAL_MS = 10*60*1000;
    public static final String NO_REGION = "";

    @DynamoDBHashKey(attributeName = "host_id")
    private String hostId;

    @DynamoDBAttribute(attributeName = "ttl")
    private long ttl;

    @DynamoDBAttribute(attributeName = "expiration_minute_utc")
    private String expirationMinuteUtc;

    @DynamoDBAttribute(attributeName = "region")
    private String region;

    @DynamoDBAttribute(attributeName = "is_test")
    private boolean isTest;

    @DynamoDBIgnore
    public boolean isNotTest(){
        return !isTest();
    }

    @DynamoDBIgnore
    public boolean isExpired() {
        return ttl < EpochSecondsNow();
    }

    @DynamoDBIgnore
    public boolean isNotExpired() {
        return !isExpired();
    }

    @DynamoDBIgnore
    public long secondsUntilExpiration() {
        return (ttl - EpochSecondsNow()) + 1;
    }

    public HeartBeat(String hostId, long ttl, String region, boolean isTest){
        this(hostId, ttl, ToReverseUtcMinuteString(ttl), region, isTest);
    }

    @Override
    public String toString() {
        return String.format(
                "%s, expirationUtc: %s, hostId: %s, ttl: %d, expirationMinuteUtc: %s, region: %s, isTest: %s, isExpired: %s",
                getClass().getSimpleName(),
                ToUtcString(ttl),
                EmptyWhenNull(hostId),
                ttl,
                EmptyWhenNull(expirationMinuteUtc),
                EmptyWhenNull(region),
                isTest,
                isExpired()
        );
    }

    public static HeartBeat parse(String jsonString) throws ParseException {
        if (jsonString == null || jsonString.trim().isEmpty()){
            throw new ParseException("Empty input", 0);
        }

        JsonNode json = parseJson(jsonString);
        val hostId = parseHostId(json);
        val intervalMs = parseIntervalMs(json);

        return new HeartBeat(hostId, EpochSecondsPlusMs(intervalMs), NO_REGION, false);
    }

    private static JsonNode parseJson(String requestBody) throws ParseException{
        try {
            return JsonNodeHelper.parse(requestBody);
        } catch (IOException e) {
            log.debug("Invalid json", e);
            throw new ParseException("Invalid json", 0);
        }
    }

    private static String parseHostId(JsonNode body) throws ParseException {
        val result = readString(body, "hostId");

        if (!StringUtils.isAlphanumeric(result)){
            throw new ParseException("Invalid hostId", 0);
        }

        if (result.length() > 100){
            throw new ParseException("Invalid hostId", 0);
        }

        return result;
    }

    private static int parseIntervalMs(JsonNode body) throws ParseException {
        val result = readInt(body, "intervalMs", HeartBeat.DEFAULT_INTERVAL_MS);

        if (result < HeartBeat.MIN_INTERVAL_MS ||
                result > HeartBeat.MAX_INTERVAL_MS){
            throw new ParseException("Invalid intervalMs", 0);
        }

        return result;
    }
}
