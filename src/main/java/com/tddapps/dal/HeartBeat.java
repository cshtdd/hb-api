package com.tddapps.dal;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static com.tddapps.utils.StringExtensions.EmptyWhenNull;

@DynamoDBTable(tableName = "hb-api-dev-heartbeats")
public class HeartBeat {
    @DynamoDBHashKey(attributeName = "host_id")
    private String hostId;
    @DynamoDBAttribute(attributeName = "expiration_utc_datetime")
    private Date expirationUtc;

    public HeartBeat(){ }
    public HeartBeat(String hostId, Date expirationUtc){
        this.hostId = hostId;
        this.expirationUtc = expirationUtc;
    }

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

    @Override
    public String toString() {
        Instant instant = getExpirationUtc().toInstant();
        ZonedDateTime utcDatetime = ZonedDateTime.ofInstant(instant, ZoneId.of("UTC"));

        return String.format(
                "%s, expirationUtc: %s, hostId: %s",
                getClass().getSimpleName(),
                utcDatetime.format(DateTimeFormatter.ISO_DATE_TIME),
                EmptyWhenNull(hostId)
        );
    }
}
