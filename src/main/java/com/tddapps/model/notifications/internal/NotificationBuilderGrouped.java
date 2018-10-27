package com.tddapps.model.notifications.internal;

import com.tddapps.model.heartbeats.HeartBeat;
import com.tddapps.model.heartbeats.HeartBeatChangeEvent;
import com.tddapps.model.notifications.HeartBeatChangeEventNotificationBuilder;
import com.tddapps.model.notifications.HeartBeatNotificationBuilder;
import com.tddapps.model.notifications.Notification;
import com.tddapps.model.notifications.NotificationMetadata;
import lombok.val;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

public class NotificationBuilderGrouped implements HeartBeatChangeEventNotificationBuilder {
    private final HeartBeatNotificationBuilder notificationBuilder;

    public NotificationBuilderGrouped(HeartBeatNotificationBuilder notificationBuilder) {
        this.notificationBuilder = notificationBuilder;
    }

    @Override
    public Notification[] build(HeartBeatChangeEvent[] events) {
        return Arrays.stream(events)
                .collect(groupingBy(HeartBeatChangeEvent::getType))
                .values()
                .stream()
                .map(this::BuildSingleNotification)
                .flatMap(List::stream)
                .toArray(Notification[]::new);
    }

    private List<Notification> BuildSingleNotification(List<HeartBeatChangeEvent> events){
        val header = events.get(0).type;
        val heartBeats = events
                .stream()
                .map(HeartBeatChangeEvent::getHeartBeat)
                .toArray(HeartBeat[]::new);

        val result = notificationBuilder.build(new NotificationMetadata(header), heartBeats);
        return Arrays.stream(result).collect(Collectors.toList());
    }
}
