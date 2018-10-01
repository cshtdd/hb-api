package com.tddapps.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.tddapps.ioc.IocContainer;
import com.tddapps.model.DalException;
import com.tddapps.model.HeartBeatRepository;
import com.tddapps.model.SettingsReader;
import com.tddapps.utils.NowReader;
import lombok.val;

import static com.tddapps.utils.DateExtensions.EpochSecondsNow;
import static com.tddapps.utils.DateExtensions.ToReverseUtcMinuteString;

public class HeartBeatExpirator implements RequestHandler<String, Boolean> {
    private final HeartBeatRepository heartBeatRepository;
    private final SettingsReader settingsReader;
    private final NowReader nowReader;

    public HeartBeatExpirator(){
        this(
                IocContainer.getInstance().Resolve(HeartBeatRepository.class),
                IocContainer.getInstance().Resolve(SettingsReader.class),
                IocContainer.getInstance().Resolve(NowReader.class)
        );
    }

    public HeartBeatExpirator(HeartBeatRepository heartBeatRepository, SettingsReader settingsReader, NowReader nowReader) {
        this.heartBeatRepository = heartBeatRepository;
        this.settingsReader = settingsReader;
        this.nowReader = nowReader;
    }

    @Override
    public Boolean handleRequest(String input, Context context) {
        return null;
    }
}
