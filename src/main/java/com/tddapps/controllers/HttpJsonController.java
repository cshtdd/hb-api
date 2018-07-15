package com.tddapps.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Map;

public class HttpJsonController {
    private final HttpJsonAction action;

    private static final Logger LOG = LogManager.getLogger(HttpJsonController.class);

    public HttpJsonController(HttpJsonAction action) {
        this.action = action;
    }

    public HttpJsonResponse process(Map<String, Object> input){
        String requestBody = input.getOrDefault("body", "").toString();

        LOG.debug(String.format("Body: %s", requestBody));

        if (requestBody == null ||
            requestBody.isEmpty() ||
            requestBody.trim().isEmpty()){
            return new HttpJsonResponse(400, "Empty request body");
        }

        JsonNode body;
        ObjectMapper mapper = new ObjectMapper();
        try {
            body = mapper.readTree(requestBody);
        } catch (IOException e) {
            return new HttpJsonResponse(400, "Invalid json in request body");
        }

        Object parsedBody;

        try {
            parsedBody = action.parse(body);
        } catch (BodyParseException e) {
            return new HttpJsonResponse(400, e.getMessage());
        }

        return action.process(parsedBody);
    }
}

