package com.tddapps.model;

import java.util.Arrays;

public class HeartBeatNotificationBuilderOneToOneStub implements HeartBeatNotificationBuilder {
    @Override
    public Notification[] build(HeartBeatChangeEvent[] events) {
        if (events == null){
            return new Notification[0];
        }

        return Arrays.stream(events)
                .map(HeartBeatNotificationBuilderOneToOneStub::toNotification)
                .toArray(Notification[]::new);
    }

    private static Notification toNotification(HeartBeatChangeEvent input){
        return new Notification(
                "S-" + input.heartBeat.getHostId() + "-" + input.type,
                "M-" + input.heartBeat.getHostId() + "-" + input.type
        );
    }

    @Deprecated
    @Override
    public Notification[] build(HeartBeat[] heartBeats) {
        if (heartBeats == null){
            return new Notification[0];
        }

        return Arrays.stream(heartBeats)
                .map(HeartBeatNotificationBuilderOneToOneStub::toNotification)
                .toArray(Notification[]::new);
    }

    @Deprecated
    private static Notification toNotification(HeartBeat input){
        return new Notification(
                "S-" + input.getHostId(),
                "M-" + input.getHostId()
        );
    }
}
