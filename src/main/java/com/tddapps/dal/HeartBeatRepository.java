package com.tddapps.dal;

public interface HeartBeatRepository {
    void Save(HeartBeat heartBeat) throws DalException;
    HeartBeat[] All() throws DalException;
}
