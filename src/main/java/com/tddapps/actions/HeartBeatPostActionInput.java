package com.tddapps.actions;

import com.tddapps.model.HeartBeat;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;

import java.util.Objects;

import static com.tddapps.utils.DateExtensions.UtcNowPlusMs;
import static com.tddapps.utils.StringExtensions.*;

@Data
public class HeartBeatPostActionInput {
    public static final int MIN_INTERVAL_MS = 1000;
    public static final int MAX_INTERVAL_MS = 12*60*60*1000;
    public static final int DEFAULT_INTERVAL_MS = 10*60*1000;

    @NonNull
    private final String hostId;
    private final int intervalMs;

    @Override
    public String toString() {
        return String.format(
                "%s, intervalMs: %d, hostId: %s",
                getClass().getSimpleName(),
                getIntervalMs(),
                EmptyWhenNull(hostId)
        );
    }

    public HeartBeat toHeartBeat() {
        return new HeartBeat(hostId, UtcNowPlusMs(intervalMs), false);
    }
}
