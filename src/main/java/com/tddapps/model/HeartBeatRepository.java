package com.tddapps.model;

public interface HeartBeatRepository {
    void Save(HeartBeat heartBeat) throws DalException;
    HeartBeat[] All() throws DalException;
}
