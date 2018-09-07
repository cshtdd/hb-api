package com.tddapps.model;

public interface HeartBeatNotificationBuilder {
    Notification[] build(HeartBeat[] heartBeats);
}
