package com.tddapps.actions;

public class HeartBeatPostActionInput {
    private final String hostId;

    public HeartBeatPostActionInput(String hostId) {
        this.hostId = hostId;
    }

    public String getHostId() {
        return hostId;
    }
}
