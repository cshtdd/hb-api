package com.tddapps.model;
import java.util.Arrays;

public class RequestHandlerHelperCurrentRegion implements RequestHandlerHelper {
    private final SettingsReader settingsReader;

    public RequestHandlerHelperCurrentRegion(SettingsReader settingsReader) {
        this.settingsReader = settingsReader;
    }

    @Override
    public HeartBeat[] filter(HeartBeat[] heartBeats) {
        return readHeartBeatsFromCurrentRegion(heartBeats);
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
