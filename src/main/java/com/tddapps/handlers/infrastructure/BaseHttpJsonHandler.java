package com.tddapps.handlers.infrastructure;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.tddapps.controllers.HttpJsonController;
import lombok.extern.log4j.Log4j2;
import lombok.val;

import java.util.Map;

@Log4j2
public abstract class BaseHttpJsonHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {
    protected abstract HttpJsonController getController();

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        log.debug(String.format("Input: %s", input));

        val response = getController().process(input);

        log.debug(String.format("StatusCode: %s, ResponseBody: %s", response.getStatusCode(), response.getBody()));

        return ApiGatewayResponse.builder()
                .setStatusCode(response.getStatusCode())
                .setObjectBody(response.getBody())
                .build();
    }
}
