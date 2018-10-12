package com.tddapps.model.notifications;

import com.tddapps.model.heartbeats.HeartBeat;

public interface HeartBeatNotificationBuilder {
    Notification[] build(NotificationMetadata metadata, HeartBeat[] heartBeats);
}
