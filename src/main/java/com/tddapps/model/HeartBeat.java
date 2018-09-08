package com.tddapps.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

import static com.tddapps.utils.DateExtensions.*;
import static com.tddapps.utils.StringExtensions.EmptyWhenNull;

@DynamoDBTable(tableName = "heartbeats")
@EqualsAndHashCode
public class HeartBeat implements Cloneable{
    @Getter
    @Setter
    @DynamoDBHashKey(attributeName = "host_id")
    private String hostId;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = "is_test")
    private boolean isTest;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = "expiration_utc_datetime")
    private Date expirationUtc;

    public HeartBeat(){ }
    public HeartBeat(String hostId, Date expirationUtc){
        this(hostId, expirationUtc, false);
    }
    public HeartBeat(String hostId, Date expirationUtc, boolean isTest){
        this.hostId = hostId;
        this.expirationUtc = expirationUtc;
        this.isTest = isTest;
    }
    protected HeartBeat(HeartBeat that){
        this(that.hostId, that.expirationUtc, that.isTest);
    }

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
        return new HeartBeat(this);
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
