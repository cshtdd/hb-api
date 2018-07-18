package com.tddapps.handlers.infrastructure;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.tddapps.controllers.HttpJsonController;
import com.tddapps.controllers.HttpJsonResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public abstract class BaseHttpJsonHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {
    private static final Logger LOG = LogManager.getLogger(BaseHttpJsonHandler.class);

    protected abstract HttpJsonController getController();

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        LOG.debug(String.format("Input: %s", input));

        HttpJsonResponse response = getController().process(input);

        LOG.debug(String.format("StatusCode: %s, ResponseBody: %s", response.getStatusCode(), response.getBody()));

        return ApiGatewayResponse.builder()
                .setStatusCode(response.getStatusCode())
                .setObjectBody(response.getBody())
                .build();
    }
}
