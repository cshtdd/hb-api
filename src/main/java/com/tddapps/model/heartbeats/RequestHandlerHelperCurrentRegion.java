package com.tddapps.model.heartbeats;
import com.tddapps.model.infrastructure.Settings;
import com.tddapps.model.infrastructure.SettingsReader;
import lombok.extern.log4j.Log4j2;
import java.util.Arrays;

@Log4j2
public class RequestHandlerHelperCurrentRegion implements RequestHandlerHelper {
    private final SettingsReader settingsReader;

    public RequestHandlerHelperCurrentRegion(SettingsReader settingsReader) {
        this.settingsReader = settingsReader;
    }

    @Override
    public HeartBeat[] filter(HeartBeat[] heartBeats) {
        return Arrays.stream(heartBeats)
                .filter(HeartBeat::isNotTest)
                .filter(this::heartBeatLastUpdatedInCurrentRegion)
                .distinct()
                .toArray(HeartBeat[]::new);
    }

    private boolean heartBeatLastUpdatedInCurrentRegion(HeartBeat hb){
        return ReadRegion().equals(hb.getRegion());
    }

    private String ReadRegion() {
        return settingsReader.ReadString(Settings.AWS_REGION);
    }
}
