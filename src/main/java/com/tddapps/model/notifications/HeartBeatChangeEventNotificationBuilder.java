package com.tddapps.model.notifications;

import com.tddapps.model.HeartBeatChangeEvent;
import com.tddapps.model.notifications.Notification;

public interface HeartBeatChangeEventNotificationBuilder {
    Notification[] build(HeartBeatChangeEvent[] events);
}
