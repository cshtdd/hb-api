package com.tddapps.model;

import java.util.Arrays;

import static com.tddapps.utils.StringExtensions.EmptyWhenNull;

public class HeartBeatNotificationBuilderOneToOneStub implements HeartBeatNotificationBuilder {
    @Override
    public Notification[] build(NotificationMetadata metadata, HeartBeat[] heartBeats) {
        if (heartBeats == null){
            return new Notification[0];
        }

        return Arrays.stream(heartBeats)
                .map(hb -> new Notification(
                        "SS-" + hb.getHostId(),
                        "MM-" + hb.getHostId() + "-" + getSubject(metadata)
                ))
                .toArray(Notification[]::new);
    }

    private static String getSubject(NotificationMetadata metadata) {
        if (metadata == null){
            return "NO_METADATA";
        }

        return EmptyWhenNull(metadata.getSubject());
    }
}
