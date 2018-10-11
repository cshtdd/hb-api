package com.tddapps.model;

import lombok.Data;
import lombok.NonNull;

@Data
public class HeartBeatChangeEvent {
    @NonNull
    public String type;
    @NonNull
    public HeartBeat heartBeat;

    @Override
    public String toString(){
        return String.format(
                "%s, type: %s, hostId: %s",
                getClass().getSimpleName(), type, heartBeat.getHostId());
    }
}
