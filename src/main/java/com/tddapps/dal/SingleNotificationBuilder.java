package com.tddapps.dal;

public class SingleNotificationBuilder implements HeartBeatNotificationBuilder {
    @Override
    public Notification[] build(HeartBeat[] heartBeats) {
        return new Notification[0];
    }
}
