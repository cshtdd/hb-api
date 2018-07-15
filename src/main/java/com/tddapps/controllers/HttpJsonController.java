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
        String requestBody = readBodyFrom(input);

        LOG.debug(String.format("Body: %s", requestBody));

        if (requestBody.isEmpty() ||
            requestBody.trim().isEmpty()){
            return HttpJsonResponse.BadRequestWithMessage("Empty Request Body");
        }

        JsonNode body;
        ObjectMapper mapper = new ObjectMapper();
        try {
            body = mapper.readTree(requestBody);
        } catch (IOException e) {
            return HttpJsonResponse.BadRequestWithMessage("Invalid json in request body");
        }

        Object parsedBody;

        try {
            parsedBody = action.parse(body);
        } catch (BodyParseException e) {
            return new HttpJsonResponse<>(400, e.getMessage());
        }

        return action.process(parsedBody);
    }

    private String readBodyFrom(Map<String, Object> input){
        Object bodyObject = input.getOrDefault("body", "");

        if (bodyObject == null){
            bodyObject = "";
        }

        return bodyObject.toString();
    }
}

