package com.tddapps.model;

public interface HeartBeatChangeEventNotificationBuilder {
    Notification[] build(HeartBeatChangeEvent[] events);
}
