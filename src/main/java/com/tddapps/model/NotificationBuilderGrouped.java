package com.tddapps.model;

import com.tddapps.utils.NowReader;
import lombok.val;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.tddapps.utils.DateExtensions.*;
import static java.util.stream.Collectors.groupingBy;

public class NotificationBuilderGrouped implements HeartBeatChangeEventNotificationBuilder {
    @Deprecated
    private final NowReader nowReader;
    private final HeartBeatNotificationBuilder notificationBuilder;

    public NotificationBuilderGrouped(NowReader nowReader, HeartBeatNotificationBuilder notificationBuilder) {
        this.nowReader = nowReader;
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

    @Deprecated
    private List<Notification> BuildSingleNotificationWithHeader(String header, HeartBeat[] heartBeats) {
        if (isEmpty(heartBeats)){
            return NoNotifications();
        }

        val subject = String.format("%s [%s]", header, getHostNames(heartBeats));
        val message = String.format("%s\n\n%s\n--\nNotification Built: %s\n--",
                subject,
                getHeartBeatDetails(heartBeats),
                ToUtcString(nowReader.ReadUtc())
        );
        val result = new Notification(subject, message);

        return new ArrayList<Notification>(){{
            add(result);
        }};
    }

    @Deprecated
    private static String getHeartBeatDetails(HeartBeat[] heartBeats) {
        return Arrays.stream(heartBeats)
                .map(HeartBeat::toString)
                .reduce((a, b) -> String.format("%s\n%s", a, b))
                .orElse("");
    }

    @Deprecated
    private static String getHostNames(HeartBeat[] heartBeats) {
        return Arrays.stream(heartBeats)
                .map(HeartBeat::getHostId)
                .reduce((a, b) -> String.format("%s, %s", a, b))
                .orElse("");
    }

    @Deprecated
    private static boolean isEmpty(HeartBeat[] heartBeats) {
        return heartBeats == null || heartBeats.length == 0;
    }

    private static List<Notification> NoNotifications() {
        return new ArrayList<>();
    }
}
