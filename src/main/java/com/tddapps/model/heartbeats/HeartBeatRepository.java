package com.tddapps.model.heartbeats;

import com.tddapps.model.DalException;

public interface HeartBeatRepository {
    void Save(HeartBeat[] heartBeat) throws DalException;
    void Delete(HeartBeat[] heartBeats) throws DalException;
    HeartBeat[] Read(String expirationMinuteUtc, int maxCount) throws DalException;
    HeartBeat[] Read(String[] hostIds) throws DalException;
}
