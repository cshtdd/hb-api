package com.tddapps.dal;

import java.util.Arrays;

public class HeartBeatNotificationBuilderOneToOneStub implements HeartBeatNotificationBuilder {
    @Override
    public Notification[] build(HeartBeat[] heartBeats) {
        if (heartBeats == null){
            return new Notification[0];
        }

        return Arrays.stream(heartBeats)
                .map(HeartBeatNotificationBuilderOneToOneStub::toNotification)
                .toArray(Notification[]::new);
    }

    private static Notification toNotification(HeartBeat input){
        return new Notification(
                "S-" + input.getHostId(),
                "M-" + input.getHostId()
        );
    }
}
