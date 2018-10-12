package com.tddapps.model.notifications;

import com.tddapps.model.HeartBeat;

public interface HeartBeatNotificationBuilder {
    Notification[] build(NotificationMetadata metadata, HeartBeat[] heartBeats);
}
