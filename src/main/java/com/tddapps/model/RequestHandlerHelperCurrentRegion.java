package com.tddapps.model;
import lombok.extern.log4j.Log4j2;
import lombok.val;

import java.util.Arrays;

@Log4j2
public class RequestHandlerHelperCurrentRegion implements RequestHandlerHelper {
    private final SettingsReader settingsReader;

    public RequestHandlerHelperCurrentRegion(SettingsReader settingsReader) {
        this.settingsReader = settingsReader;
    }

    @Override
    public HeartBeat[] filter(HeartBeat[] heartBeats) {
        logHeartBeats(heartBeats);
        val result = filterInternal(heartBeats);
        logMismatch(heartBeats, result);
        return result;
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

    private HeartBeat[] filterInternal(HeartBeat[] heartBeats){
        return Arrays.stream(heartBeats)
                .filter(HeartBeat::isNotTest)
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
