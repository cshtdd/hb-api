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
    private final int MAX_COUNT_TO_PROCESS = 25;

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
        try {
            val expiredHeartBeats = readExpiredHeartBeats();
            val heartBeatsToDelete = readHeartBeatsFromCurrentRegion(expiredHeartBeats);

            heartBeatRepository.Delete(heartBeatsToDelete);

            return true;
        } catch (DalException e) {
            return false;
        }
    }

    private HeartBeat[] readExpiredHeartBeats() throws DalException {
        val ttlNow = nowReader.ReadEpochSecond();
        val minuteStringNow = ToReverseUtcMinuteString(ttlNow);
        return heartBeatRepository.ReadOlderThan(minuteStringNow, ttlNow, MAX_COUNT_TO_PROCESS);
    }

    private HeartBeat[] readHeartBeatsFromCurrentRegion(HeartBeat[] heartBeats){
        return Arrays.stream(heartBeats)
                .filter(this::heartBeatLastUpdatedInCurrentRegion)
                .toArray(HeartBeat[]::new);
    }

    private boolean heartBeatLastUpdatedInCurrentRegion(HeartBeat hb){
        return ReadRegion().equals(hb.getRegion());
    }

    private String ReadRegion() {
        return settingsReader.ReadString(Settings.AWS_REGION);
    }
}
