package com.tddapps.model.heartbeats;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;

import static com.tddapps.utils.DateExtensions.*;
import static com.tddapps.utils.StringExtensions.EmptyWhenNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBTable(tableName = "heartbeats")
@Log4j2
public class HeartBeat{
    private static final int MAX_SQS_DELAY_SECONDS = 900;

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
    @JsonIgnore
    public boolean isNotTest(){
        return !isTest();
    }

    @DynamoDBIgnore
    @JsonIgnore
    public boolean isExpired() {
        return ttl < EpochSecondsNow();
    }

    @DynamoDBIgnore
    @JsonIgnore
    public boolean isNotExpired() {
        return !isExpired();
    }

    @DynamoDBIgnore
    @JsonIgnore
    public long secondsUntilExpiration() {
        if (isExpired()){
            return 0;
        }

        val result = (ttl - EpochSecondsNow()) + 1;

        return Math.min(MAX_SQS_DELAY_SECONDS, result);
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
}
