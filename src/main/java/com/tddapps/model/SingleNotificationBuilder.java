package com.tddapps.model;

import com.tddapps.utils.NowReader;
import lombok.val;

import java.util.Arrays;

import static com.tddapps.utils.DateExtensions.*;

public class SingleNotificationBuilder implements HeartBeatNotificationBuilder {
    private final NowReader nowReader;

    public SingleNotificationBuilder(NowReader nowReader) {
        this.nowReader = nowReader;
    }

    @Override
    public Notification[] build(HeartBeat[] heartBeats) {
        if (isEmpty(heartBeats)){
            return NoNotifications();
        }

        val header = heartBeats[0].isExpired() ? "Hosts missing" : "Hosts registered";
        val subject = String.format("%s [%s]", header, getHostNames(heartBeats));
        val message = String.format("%s\n\n%s\n--\nNotification Built: %s\n--",
                subject,
                getHeartBeatDetails(heartBeats),
                ToUtcString(nowReader.ReadUtc())
        );

        return new Notification[]{
                new Notification(subject,message)
        };
    }

    private String getHeartBeatDetails(HeartBeat[] heartBeats) {
        return Arrays.stream(heartBeats)
                .map(HeartBeat::toString)
                .reduce((a, b) -> String.format("%s\n%s", a, b))
                .orElse("");
    }

    private String getHostNames(HeartBeat[] heartBeats) {
        return Arrays.stream(heartBeats)
                .map(HeartBeat::getHostId)
                .reduce((a, b) -> String.format("%s, %s", a, b))
                .orElse("");
    }

    private boolean isEmpty(HeartBeat[] heartBeats) {
        return heartBeats == null || heartBeats.length == 0;
    }

    private Notification[] NoNotifications() {
        return new Notification[0];
    }
}
