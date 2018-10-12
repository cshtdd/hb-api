package com.tddapps.model;

import java.util.Arrays;

public class HeartBeatChangeEventNotificationBuilderOneToOneStub implements HeartBeatChangeEventNotificationBuilder {
    @Override
    public Notification[] build(HeartBeatChangeEvent[] events) {
        if (events == null){
            return new Notification[0];
        }

        return Arrays.stream(events)
                .map(HeartBeatChangeEventNotificationBuilderOneToOneStub::toNotification)
                .toArray(Notification[]::new);
    }

    private static Notification toNotification(HeartBeatChangeEvent input){
        return new Notification(
                "S-" + input.heartBeat.getHostId(),
                "M-" + input.heartBeat.getHostId() + "-" + input.type
        );
    }
}
