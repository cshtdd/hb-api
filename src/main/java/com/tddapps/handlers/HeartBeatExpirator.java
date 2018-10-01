package com.tddapps.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.tddapps.ioc.IocContainer;
import com.tddapps.model.HeartBeatRepository;
import com.tddapps.model.SettingsReader;

public class HeartBeatExpirator implements RequestHandler<String, Boolean> {
    private final HeartBeatRepository heartBeatRepository;
    private final SettingsReader settingsReader;

    public HeartBeatExpirator(){
        this(
                IocContainer.getInstance().Resolve(HeartBeatRepository.class),
                IocContainer.getInstance().Resolve(SettingsReader.class)
        );
    }

    public HeartBeatExpirator(HeartBeatRepository heartBeatRepository, SettingsReader settingsReader) {
        this.heartBeatRepository = heartBeatRepository;
        this.settingsReader = settingsReader;
    }

    @Override
    public Boolean handleRequest(String input, Context context) {
        return null;
    }
}
