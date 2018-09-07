package com.tddapps.dal;

public interface HeartBeatNotificationBuilder {
    Notification[] build(HeartBeat[] heartBeats);
}
