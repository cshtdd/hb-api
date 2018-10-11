package com.tddapps.model;

public interface HeartBeatNotificationBuilder {
    Notification[] build(HeartBeatChangeEvent[] events);
    @Deprecated
    Notification[] build(HeartBeat[] heartBeats);
}
