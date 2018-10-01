package com.tddapps.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.tddapps.ioc.IocContainer;
import com.tddapps.model.*;
import com.tddapps.utils.NowReader;
import lombok.val;

import java.util.Arrays;

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
        val ttlNow = nowReader.ReadEpochSecond();
        val currentRegion = settingsReader.ReadString(Settings.AWS_REGION);

        try {
            val heartBeats = heartBeatRepository.ReadOlderThan(
                    ToReverseUtcMinuteString(ttlNow), ttlNow, 25);

            val heartBeatsToDelete = Arrays.stream(heartBeats)
                    .filter(hb -> hb.getRegion().equals(currentRegion))
                    .toArray(HeartBeat[]::new);

            heartBeatRepository.Delete(heartBeatsToDelete);
        } catch (DalException e) {
            return false;
        }

        return true;
    }
}
