package com.tddapps.model;

public interface HeartBeatNotificationBuilder {
    Notification[] build(NotificationMetadata metadata, HeartBeat[] heartBeats);
}
