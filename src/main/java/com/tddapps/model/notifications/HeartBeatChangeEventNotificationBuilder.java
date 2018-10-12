package com.tddapps.model.notifications;

import com.tddapps.model.heartbeats.HeartBeatChangeEvent;

public interface HeartBeatChangeEventNotificationBuilder {
    Notification[] build(HeartBeatChangeEvent[] events);
}
