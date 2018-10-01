package com.tddapps.model;

public class HeartBeatListHandlerHelperCurrentRegion implements HeartBeatListHandlerHelper {
    private final SettingsReader settingsReader;

    public HeartBeatListHandlerHelperCurrentRegion(SettingsReader settingsReader) {
        this.settingsReader = settingsReader;
    }

    @Override
    public HeartBeat[] filter(HeartBeat[] heartBeats) {
        return new HeartBeat[0];
    }
}
