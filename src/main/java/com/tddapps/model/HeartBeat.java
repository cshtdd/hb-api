package com.tddapps.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import static com.tddapps.utils.DateExtensions.*;
import static com.tddapps.utils.StringExtensions.EmptyWhenNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBTable(tableName = "heartbeats")
public class HeartBeat implements Cloneable{
    @DynamoDBHashKey(attributeName = "host_id")
    private String hostId;

    @DynamoDBAttribute(attributeName = "expiration_utc_datetime")
    private Date expirationUtc;

    @DynamoDBAttribute(attributeName = "is_test")
    private boolean isTest;

    @DynamoDBIgnore
    public boolean isNotTest(){
        return !isTest();
    }

    @Override
    public String toString() {
        return String.format(
                "%s, expirationUtc: %s, hostId: %s, isTest: %s",
                getClass().getSimpleName(),
                ToUtcString(getExpirationUtc(), "null"),
                EmptyWhenNull(hostId),
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
        HeartBeat result = (HeartBeat)this.clone();

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
}
