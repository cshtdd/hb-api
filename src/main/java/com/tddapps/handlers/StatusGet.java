package com.tddapps.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.tddapps.handlers.infrastructure.ApiGatewayResponse;

import java.util.Map;

@SuppressWarnings("unused")
public class StatusGet implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {
    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        return ApiGatewayResponse.builder()
                .setStatusCode(200)
                .setObjectBody("OK")
                .build();
    }
}
