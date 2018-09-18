package com.tddapps.handlers.infrastructure;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import lombok.extern.log4j.Log4j2;
import lombok.val;

import java.util.Map;

@Log4j2
public abstract class ApiGatewayHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {
    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context){
        log.debug(String.format("Input: %s", input));

        val result = processRequest(input);

        log.info(String.format("StatusCode: %s, ResponseBody: %s", result.getStatusCode(), result.getBody()));

        return result;
    }

    protected abstract ApiGatewayResponse processRequest(Map<String, Object> input);
}
