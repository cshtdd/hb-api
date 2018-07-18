package com.tddapps.actions;

import java.util.Objects;

import static com.tddapps.utils.StringExtensions.*;

public class HeartBeatPostActionInput {
    private final String hostId;

    public HeartBeatPostActionInput(String hostId) {
        this.hostId = hostId;
    }

    public String getHostId() {
        return hostId;
    }

    @Override
    public String toString() {
        return String.format("%s, hostId: %s", getClass().getSimpleName(), EmptyWhenNull(hostId));
    }

    @Override
    public int hashCode() {
        return Objects.hash(hostId);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof HeartBeatPostActionInput)){
            return false;
        }

        HeartBeatPostActionInput that = (HeartBeatPostActionInput)obj;

        return EmptyWhenNull(this.hostId).equals(EmptyWhenNull(that.hostId));
    }
}
