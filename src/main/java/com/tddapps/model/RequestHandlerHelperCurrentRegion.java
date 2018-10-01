package com.tddapps.model;

public class RequestHandlerHelperCurrentRegion implements RequestHandlerHelper {
    private final SettingsReader settingsReader;

    public RequestHandlerHelperCurrentRegion(SettingsReader settingsReader) {
        this.settingsReader = settingsReader;
    }

    @Override
    public HeartBeat[] filter(HeartBeat[] heartBeats) {
        return new HeartBeat[0];
    }
}
