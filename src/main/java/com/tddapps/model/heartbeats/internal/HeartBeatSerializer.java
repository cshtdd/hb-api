package com.tddapps.model.heartbeats.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.tddapps.model.heartbeats.HeartBeat;
import com.tddapps.model.heartbeats.HeartBeatJsonConverter;
import com.tddapps.model.heartbeats.HeartBeatParser;
import com.tddapps.utils.JsonNodeHelper;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.ParseException;

import static com.tddapps.utils.DateExtensions.EpochSecondsPlusMs;
import static com.tddapps.utils.JsonNodeHelper.readInt;
import static com.tddapps.utils.JsonNodeHelper.readString;

@Log4j2
public class HeartBeatSerializer implements HeartBeatParser, HeartBeatJsonConverter {
    private static final int MIN_INTERVAL_MS = 1000;
    private static final int MAX_INTERVAL_MS = 12*60*60*1000;
    public static final int DEFAULT_INTERVAL_MS = 10*60*1000;
    private static final String NO_REGION = "";

    @Override
    public HeartBeat parseUnsanitizedJson(String jsonString) throws ParseException {
        if (jsonString == null || jsonString.trim().isEmpty()){
            throw new ParseException("Empty input", 0);
        }

        JsonNode json = parseJson(jsonString);
        val hostId = parseHostId(json);
        val intervalMs = parseIntervalMs(json);

        return new HeartBeat(hostId, EpochSecondsPlusMs(intervalMs), NO_REGION, false);
    }

    private static JsonNode parseJson(String requestBody) throws ParseException{
        try {
            return JsonNodeHelper.parse(requestBody);
        } catch (IOException e) {
            log.debug("Invalid json", e);
            throw new ParseException("Invalid json", 0);
        }
    }

    private static String parseHostId(JsonNode body) throws ParseException {
        val result = readString(body, "hostId");

        if (!StringUtils.isAlphanumeric(result)){
            throw new ParseException("Invalid hostId", 0);
        }

        if (result.length() > 100){
            throw new ParseException("Invalid hostId", 0);
        }

        return result;
    }

    private static int parseIntervalMs(JsonNode body) throws ParseException {
        val result = readInt(body, "intervalMs", DEFAULT_INTERVAL_MS);

        if (result < MIN_INTERVAL_MS ||
                result > MAX_INTERVAL_MS){
            throw new ParseException("Invalid intervalMs", 0);
        }

        return result;
    }

    @Override
    public HeartBeat fromJson(String jsonString) throws ParseException {
        return null;
    }

    @Override
    public String toJson(HeartBeat heartBeat) {
        return null;
    }
}
