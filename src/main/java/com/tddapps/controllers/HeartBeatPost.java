package com.tddapps.controllers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tddapps.controllers.response.ApiGatewayResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Map;

public class HeartBeatPost implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {
    private static final Logger LOG = LogManager.getLogger(HeartBeatPost.class);

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        LOG.debug(String.format("Input: %s", input));

        String requestBody = input.getOrDefault("body", "").toString();

        LOG.debug(String.format("Body: %s", requestBody));

        //TODO: validate body is not empty

        JsonNode body = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            body = mapper.readTree(requestBody);
        } catch (IOException e) {
            return ApiGatewayResponse.builder()
                    .setStatusCode(400)
                    .setObjectBody("Invalid Request Body")
                    .build();
        }
        String hostId = body.get("hostId").asText();

        if (hostId == null || hostId.length() == 0){
            return ApiGatewayResponse.builder()
                    .setStatusCode(400)
                    .setObjectBody("Invalid hostId")
                    .build();
        }

        LOG.info(String.format("hostId: %s", hostId));

        return ApiGatewayResponse.builder()
                .setStatusCode(200)
                .setObjectBody("OK")
                .build();
    }
}
