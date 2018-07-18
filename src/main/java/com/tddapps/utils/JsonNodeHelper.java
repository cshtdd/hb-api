package com.tddapps.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonNodeHelper {
    public static JsonNode parse(String str) throws IOException {
        final ObjectMapper jsonDeserializer = new ObjectMapper();
        return jsonDeserializer.readTree(str);
    }

    public static String readString(JsonNode body, String parameterName){
        return readString(body, parameterName, "");
    }

    public static String readString(JsonNode body, String parameterName, String defaultValue){
        JsonNode value = body.get(parameterName);

        if (value == null){
            return defaultValue;
        }

        return value.asText(defaultValue);
    }

    public static int readInt(JsonNode body, String parameterName, int defaultValue){
        JsonNode value = body.get(parameterName);

        if (value == null){
            return defaultValue;
        }

        return value.asInt(defaultValue);
    }
}
