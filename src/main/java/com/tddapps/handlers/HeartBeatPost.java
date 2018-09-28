package com.tddapps.handlers;

import com.tddapps.handlers.infrastructure.ApiGatewayHandler;
import com.tddapps.handlers.infrastructure.ApiGatewayResponse;
import com.tddapps.ioc.IocContainer;
import com.tddapps.model.DalException;
import com.tddapps.model.HeartBeat;
import com.tddapps.model.HeartBeatRepository;
import com.tddapps.model.TextMessage;
import lombok.extern.log4j.Log4j2;
import lombok.val;

import java.text.ParseException;
import java.util.Map;

@SuppressWarnings("unused")
@Log4j2
public class HeartBeatPost extends ApiGatewayHandler {
    private final HeartBeatRepository heartBeatRepository;

    public HeartBeatPost(){
        this(IocContainer.getInstance().Resolve(HeartBeatRepository.class));
    }

    public HeartBeatPost(HeartBeatRepository heartBeatRepository) {
        this.heartBeatRepository = heartBeatRepository;
    }

    @Override
    protected ApiGatewayResponse processRequest(Map<String, Object> input){
        try {
            val requestBody = readBodyFrom(input);
            //TODO figure out how to pass the region
            val heartBeat = HeartBeat.parse(requestBody);

            log.info(String.format("hostId: %s", heartBeat.getHostId()));

            heartBeatRepository.Save(heartBeat);

            return ApiGatewayResponse.builder()
                    .setStatusCode(200)
                    .setObjectBody(TextMessage.OK)
                    .build();
        }
        catch (ParseException e) {
            log.warn("Action parsing failed", e);

            return ApiGatewayResponse.builder()
                    .setStatusCode(400)
                    .setObjectBody(TextMessage.create(e.getMessage()))
                    .build();
        } catch (DalException e) {
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
