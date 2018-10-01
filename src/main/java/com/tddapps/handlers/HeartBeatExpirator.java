package com.tddapps.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.tddapps.ioc.IocContainer;
import com.tddapps.model.*;
import com.tddapps.utils.NowReader;
import lombok.extern.log4j.Log4j2;
import lombok.val;

import java.util.Arrays;
import java.util.Map;

import static com.tddapps.utils.DateExtensions.ToReverseUtcMinuteString;

@Log4j2
@SuppressWarnings("unused")
public class HeartBeatExpirator implements RequestHandler<Map<String, Object>, Boolean> {
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
    public Boolean handleRequest(Map<String, Object> input, Context context) {
        try {
            log.info("Removing expired HeartBeats");

            val expiredHeartBeats = readExpiredHeartBeats();
            logHeartBeats(expiredHeartBeats);

            val heartBeatsToDelete = readHeartBeatsFromCurrentRegion(expiredHeartBeats);
            logMismatch(expiredHeartBeats, heartBeatsToDelete);

            heartBeatRepository.Delete(heartBeatsToDelete);

            log.info("Removing expired HeartBeats Completed");
            return true;
        } catch (DalException e) {
            log.error("Removing expired HeartBeats failed", e);
            return false;
        }
    }

    private HeartBeat[] readExpiredHeartBeats() throws DalException {
        val ttlNow = nowReader.ReadEpochSecond();
        val minuteStringNow = ToReverseUtcMinuteString(ttlNow - 60);

        return heartBeatRepository.ReadOlderThan(minuteStringNow, ttlNow, MAX_COUNT_TO_PROCESS);
    }

    private HeartBeat[] readHeartBeatsFromCurrentRegion(HeartBeat[] heartBeats){
        return Arrays.stream(heartBeats)
                .filter(this::heartBeatLastUpdatedInCurrentRegion)
                .toArray(HeartBeat[]::new);
    }

    private void logHeartBeats(HeartBeat[] heartBeats) {
        for (val hb : heartBeats){
            log.info(String.format("Host missing; currentRegion: %s; %s",
                    ReadRegion(), hb.toString()));
        }
    }

    private void logMismatch(HeartBeat[] allHeartBeats, HeartBeat[] subsetCount) {
        log.info(String.format("AllHeartBeatCount: %d; CurrentRegionCount: %d;",
                allHeartBeats.length, subsetCount.length));
    }

    private boolean heartBeatLastUpdatedInCurrentRegion(HeartBeat hb){
        return ReadRegion().equals(hb.getRegion());
    }

    private String ReadRegion() {
        return settingsReader.ReadString(Settings.AWS_REGION);
    }
}
