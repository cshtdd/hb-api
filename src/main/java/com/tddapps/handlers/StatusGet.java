package com.tddapps.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.tddapps.actions.StatusGetAction;
import com.tddapps.controllers.ActionProcessException;
import com.tddapps.controllers.HttpJsonResponse;
import com.tddapps.handlers.infrastructure.ApiGatewayResponse;
import com.tddapps.ioc.IocContainer;
import lombok.extern.log4j.Log4j2;
import lombok.val;

import java.util.Map;

@SuppressWarnings("unused")
@Log4j2
public class StatusGet implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {
    private final StatusGetAction action;

    public StatusGet(){
        this(IocContainer.getInstance().Resolve(StatusGetAction.class));
    }

    public StatusGet(StatusGetAction action){
        this.action = action;
    }

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        log.debug(String.format("Input: %s", input));

        try {
            val successResponse = action.process();

            log.debug(String.format("StatusCode: %s, ResponseBody: %s", successResponse.getStatusCode(), successResponse.getBody()));

            return ApiGatewayResponse.builder()
                    .setStatusCode(successResponse.getStatusCode())
                    .setObjectBody(successResponse.getBody())
                    .build();

        } catch (ActionProcessException e) {
            log.error("Action processing failed", e);

            val errorResponse = HttpJsonResponse.ServerErrorWithMessage(e.getMessage());

            return ApiGatewayResponse.builder()
                    .setStatusCode(errorResponse.getStatusCode())
                    .setObjectBody(errorResponse.getBody())
                    .build();
        }
    }

}
