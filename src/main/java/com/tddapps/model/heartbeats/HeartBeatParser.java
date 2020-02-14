package com.tddapps.model.heartbeats;

import java.text.ParseException;

public interface HeartBeatParser {
    HeartBeat parseUnsanitizedJson(String string) throws ParseException;
}
