package com.tddapps.model;

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
import java.util.Date;

import static com.tddapps.utils.DateExtensions.*;
import static com.tddapps.utils.JsonNodeHelper.readInt;
import static com.tddapps.utils.JsonNodeHelper.readString;
import static com.tddapps.utils.StringExtensions.EmptyWhenNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBTable(tableName = "heartbeats")
@Log4j2
public class HeartBeat implements Cloneable{
    public static final int MIN_INTERVAL_MS = 1000;
    public static final int MAX_INTERVAL_MS = 12*60*60*1000;
    public static final int DEFAULT_INTERVAL_MS = 10*60*1000;

    @DynamoDBHashKey(attributeName = "host_id")
    private String hostId;

    @Deprecated
    @DynamoDBAttribute(attributeName = "expiration_utc_datetime")
    private Date expirationUtc;

    @DynamoDBAttribute(attributeName = "ttl")
    private long ttl;

    @DynamoDBAttribute(attributeName = "is_test")
    private boolean isTest;

    @DynamoDBIgnore
    public boolean isNotTest(){
        return !isTest();
    }

    @Deprecated
    public HeartBeat(String hostId, Date expirationUtc, boolean isTest){
        this(hostId, expirationUtc, 0, isTest);
    }

    @Override
    public String toString() {
        return String.format(
                "%s, expirationUtc: %s, hostId: %s, ttl: %d, isTest: %s",
                getClass().getSimpleName(),
                ToUtcString(getExpirationUtc(), "null"),
                EmptyWhenNull(hostId),
                ttl,
                isTest
        );
    }

    public boolean almostEquals(HeartBeat that) {
        if (that == null){
            return false;
        }

        if(this.isTest != that.isTest){
            return false;
        }

        if (!EmptyWhenNull(this.hostId).equals(EmptyWhenNull(that.hostId))){
            return false;
        }

        return AreAlmostEquals(this.expirationUtc, that.expirationUtc);
    }

    public Object clone(){
        return new HeartBeat(hostId, expirationUtc, isTest);
    }

    public HeartBeat clone(Date updateExpirationUtc){
        val result = (HeartBeat)this.clone();

        result.setExpirationUtc(updateExpirationUtc);

        return result;
    }

    @DynamoDBIgnore
    public boolean isExpired() {
        if (expirationUtc == null){
            return true;
        }

        if (AreAlmostEquals(UtcNow(), expirationUtc)){
            return false;
        }

        return UtcNow().compareTo(expirationUtc) >= 0;
    }

    @DynamoDBIgnore
    public boolean isNotExpired(){
        return !isExpired();
    }

    public static HeartBeat parse(String jsonString) throws ParseException {
        if (jsonString == null || jsonString.trim().isEmpty()){
            throw new ParseException("Empty input", 0);
        }

        JsonNode json = parseJson(jsonString);
        val hostId = parseHostId(json);
        val intervalMs = parseIntervalMs(json);

        return new HeartBeat(hostId, UtcNowPlusMs(intervalMs), false);
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
