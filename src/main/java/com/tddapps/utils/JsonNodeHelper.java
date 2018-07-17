package com.tddapps.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

public class JsonNodeHelper {
    public static JsonNode parse(String str) throws IOException {
        final ObjectMapper jsonDeserializer = new ObjectMapper();
        return jsonDeserializer.readTree(str);
    }
}
