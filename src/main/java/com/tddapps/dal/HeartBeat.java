package com.tddapps.dal;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import java.util.Date;

@DynamoDBTable(tableName = "hb-api-dev-heartbeats")
public class HeartBeat {
    @DynamoDBHashKey(attributeName = "host_id")
    private String hostId;
    @DynamoDBAttribute(attributeName = "expiration_utc_datetime")
    private Date expirationUtc;

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public Date getExpirationUtc() {
        return expirationUtc;
    }

    public void setExpirationUtc(Date expirationUtc) {
        this.expirationUtc = expirationUtc;
    }
}
