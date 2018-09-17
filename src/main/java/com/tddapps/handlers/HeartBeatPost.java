package com.tddapps.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.tddapps.actions.HeartBeatPostAction;
import com.tddapps.actions.response.TextMessage;
import com.tddapps.controllers.*;
import com.tddapps.handlers.infrastructure.ApiGatewayResponse;
import com.tddapps.handlers.infrastructure.BaseHttpJsonHandler;
import com.tddapps.ioc.IocContainer;
import com.tddapps.utils.JsonNodeHelper;
import lombok.extern.log4j.Log4j2;
import lombok.val;

import java.io.IOException;
import java.util.Map;

@SuppressWarnings("unused")
@Log4j2
public class HeartBeatPost implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {
    private final HeartBeatPostAction action;

    public HeartBeatPost(){
        this(IocContainer.getInstance().Resolve(HeartBeatPostAction.class));
    }

    public HeartBeatPost(HeartBeatPostAction action) {
        this.action = action;
    }

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        try {
            val requestBody = readBodyFrom(input);
            log.debug(String.format("Body: %s", requestBody));

            if (requestBody.trim().isEmpty()){
                return ApiGatewayResponse.builder()
                        .setStatusCode(400)
                        .setObjectBody(TextMessage.create("Empty Request Body"))
                        .build();
            }

            val jsonBody = JsonNodeHelper.parse(requestBody);
            val parsedBody = action.parse(jsonBody);

            val response = action.process(parsedBody);

            return ApiGatewayResponse.builder()
                    .setStatusCode(200)
                    .setObjectBody(response.getBody())
                    .build();
        } catch (IOException e) {
            log.warn("Invalid json in request body", e);
            return ApiGatewayResponse.builder()
                    .setStatusCode(400)
                    .setObjectBody(TextMessage.create("Invalid json in request body"))
                    .build();
        } catch (ActionBodyParseException e) {
            log.warn("Action parsing failed", e);
            return ApiGatewayResponse.builder()
                    .setStatusCode(400)
                    .setObjectBody(TextMessage.create(e.getMessage()))
                    .build();
        } catch (ActionProcessException e) {
            log.error("Action processing failed", e);
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .setObjectBody(TextMessage.create(e.getMessage()))
                    .build();
        }
    }

    private String readBodyFrom(Map<String, Object> input){
        val bodyObject = input.getOrDefault("body", "");

        if (bodyObject == null){
            return "";
        }

        return bodyObject.toString();
    }
}
