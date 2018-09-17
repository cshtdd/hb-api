package com.tddapps.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.tddapps.actions.HeartBeatPostActionInput;
import com.tddapps.actions.response.TextMessage;
import com.tddapps.controllers.ActionBodyParseException;
import com.tddapps.controllers.ActionProcessException;
import com.tddapps.controllers.HttpJsonResponse;
import com.tddapps.handlers.infrastructure.ApiGatewayResponse;
import com.tddapps.ioc.IocContainer;
import com.tddapps.model.DalException;
import com.tddapps.model.HeartBeatRepository;
import com.tddapps.utils.JsonNodeHelper;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Map;

import static com.tddapps.utils.JsonNodeHelper.readInt;
import static com.tddapps.utils.JsonNodeHelper.readString;

@SuppressWarnings("unused")
@Log4j2
public class HeartBeatPost implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {
    private final HeartBeatRepository heartBeatRepository;

    public HeartBeatPost(){
        this(IocContainer.getInstance().Resolve(HeartBeatRepository.class));
    }

    public HeartBeatPost(HeartBeatRepository heartBeatRepository) {

        this.heartBeatRepository = heartBeatRepository;
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
            val parsedBody = parse(jsonBody);

            val response = process(parsedBody);

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

    private HttpJsonResponse<TextMessage> process(HeartBeatPostActionInput body) throws ActionProcessException {
        log.info(String.format("hostId: %s", body.getHostId()));

        try {
            heartBeatRepository.Save(body.toHeartBeat());
        } catch (DalException e) {
            throw new ActionProcessException(e.getMessage());
        }

        return new HttpJsonResponse<>(200, TextMessage.OK);
    }

    private String readBodyFrom(Map<String, Object> input){
        val bodyObject = input.getOrDefault("body", "");

        if (bodyObject == null){
            return "";
        }

        return bodyObject.toString();
    }

    private HeartBeatPostActionInput parse(JsonNode body) throws ActionBodyParseException {
        return new HeartBeatPostActionInput(
                readHostId(body),
                readIntervalMs(body)
        );
    }

    private String readHostId(JsonNode body) throws ActionBodyParseException {
        val result = readString(body, "hostId");

        if (!StringUtils.isAlphanumeric(result)){
            throw new ActionBodyParseException("Invalid hostId");
        }

        if (result.length() > 100){
            throw new ActionBodyParseException("Invalid hostId");
        }

        return result;
    }

    private int readIntervalMs(JsonNode body) throws ActionBodyParseException {
        val result = readInt(body, "intervalMs", HeartBeatPostActionInput.DEFAULT_INTERVAL_MS);

        if (result < HeartBeatPostActionInput.MIN_INTERVAL_MS ||
                result > HeartBeatPostActionInput.MAX_INTERVAL_MS){
            throw new ActionBodyParseException("Invalid intervalMs");
        }

        return result;
    }
}
