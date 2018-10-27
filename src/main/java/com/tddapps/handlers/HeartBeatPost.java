package com.tddapps.handlers;

import com.tddapps.handlers.infrastructure.ApiGatewayHandler;
import com.tddapps.handlers.infrastructure.ApiGatewayResponse;
import com.tddapps.handlers.infrastructure.TextMessage;
import com.tddapps.ioc.IocContainer;
import com.tddapps.model.*;
import com.tddapps.model.heartbeats.HeartBeat;
import com.tddapps.model.heartbeats.HeartBeatRepository;
import com.tddapps.model.infrastructure.Settings;
import com.tddapps.model.infrastructure.SettingsReader;
import lombok.extern.log4j.Log4j2;
import lombok.val;

import java.text.ParseException;
import java.util.Map;

@SuppressWarnings("unused")
@Log4j2
public class HeartBeatPost extends ApiGatewayHandler {
    private final HeartBeatRepository heartBeatRepository;
    private final SettingsReader settingsReader;

    public HeartBeatPost(){
        this(
                IocContainer.getInstance().Resolve(HeartBeatRepository.class),
                IocContainer.getInstance().Resolve(SettingsReader.class)
        );
    }

    public HeartBeatPost(HeartBeatRepository heartBeatRepository, SettingsReader settingsReader) {
        this.heartBeatRepository = heartBeatRepository;
        this.settingsReader = settingsReader;
    }

    @Override
    protected ApiGatewayResponse processRequest(Map<String, Object> input){
        try {
            val requestBody = readBodyFrom(input);
            val heartBeat = HeartBeat.parse(requestBody);
            heartBeat.setRegion(ReadRegion());

            log.info(String.format("hostId: %s", heartBeat.getHostId()));

            heartBeatRepository.Save(new HeartBeat[]{ heartBeat });

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

    private String ReadRegion() {
        return settingsReader.ReadString(Settings.AWS_REGION);
    }

    private String readBodyFrom(Map<String, Object> input){
        val bodyObject = input.getOrDefault("body", "");

        if (bodyObject == null){
            return "";
        }

        return bodyObject.toString();
    }
}
