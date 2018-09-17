package com.tddapps.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.tddapps.handlers.infrastructure.ApiGatewayResponse;
import lombok.extern.log4j.Log4j2;
import lombok.val;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@SuppressWarnings("unused")
public class HeartBeatChange implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {
    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        val inputKeys = input.keySet().toArray();

        for (val k : inputKeys){
            log.info(String.format("%s => %s", k.toString(), input.get(k)));
        }

        return new ApiGatewayResponse(200, "", new HashMap<>(), false);
    }
}
