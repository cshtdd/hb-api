package com.tddapps.model;

import com.tddapps.utils.NowReader;

public class SingleNotificationBuilder implements HeartBeatNotificationBuilder {
    private final NowReader nowReader;

    public SingleNotificationBuilder(NowReader nowReader) {
        this.nowReader = nowReader;
    }

    @Override
    public Notification[] build(NotificationMetadata metadata, HeartBeat[] heartBeats) {
        return new Notification[0];
    }
}
