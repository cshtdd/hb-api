package com.tddapps.model;

public interface HeartBeatRepository {
    void Save(HeartBeat[] heartBeat) throws DalException;
    HeartBeat[] ReadOlderThan(String expirationMinuteUtc, long ttl, int maxCount) throws DalException;
}
