package com.tddapps.model.heartbeats;

import java.text.ParseException;

public interface HeartBeatJsonConverter {
    HeartBeat fromJson(String jsonString) throws ParseException;
    String toJson(HeartBeat heartBeat);
}
