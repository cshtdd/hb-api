package com.tddapps.model;

import com.tddapps.utils.NowReader;
import lombok.val;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.tddapps.utils.DateExtensions.*;

public class SingleNotificationBuilder implements HeartBeatNotificationBuilder {
    private final NowReader nowReader;

    public SingleNotificationBuilder(NowReader nowReader) {
        this.nowReader = nowReader;
    }

    @Override
    public Notification[] build(HeartBeat[] heartBeats) {
        val expiredHeartBeats = Arrays.stream(heartBeats)
                .filter(HeartBeat::isExpired)
                .toArray(HeartBeat[]::new);
        val notificationHostsMissing = BuildSingleNotificationWithHeader("Hosts missing", expiredHeartBeats);

        val registeredHeartBeats = Arrays.stream(heartBeats)
                .filter(HeartBeat::isNotExpired)
                .toArray(HeartBeat[]::new);
        val notificationHostsRegistered = BuildSingleNotificationWithHeader("Hosts registered", registeredHeartBeats);

        val result = new ArrayList<Notification>() {{
            addAll(notificationHostsMissing);
            addAll(notificationHostsRegistered);
        }};
        return result.toArray(new Notification[0]);
    }

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

    private List<Notification> NoNotifications() {
        return new ArrayList<>();
    }
}
