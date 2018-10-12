package com.tddapps.model.notifications;

import com.tddapps.model.heartbeats.HeartBeat;
import com.tddapps.utils.NowReader;
import lombok.val;
import java.util.Arrays;

import static com.tddapps.utils.DateExtensions.ToUtcString;

public class SingleNotificationBuilder implements HeartBeatNotificationBuilder {
    private final NowReader nowReader;

    public SingleNotificationBuilder(NowReader nowReader) {
        this.nowReader = nowReader;
    }

    @Override
    public Notification[] build(NotificationMetadata metadata, HeartBeat[] heartBeats) {
        if (isEmpty(heartBeats)){
            return NoNotifications();
        }

        val subject = String.format("%s [%s]", metadata.getSubject(), getHostNames(heartBeats));
        val message = String.format("%s\n\n%s\n--\nNotification Built: %s\n--",
                subject,
                getHeartBeatDetails(heartBeats),
                ToUtcString(nowReader.ReadUtc())
        );
        val result = new Notification(subject, message);

        return new Notification[]{ result };
    }

    private static String getHeartBeatDetails(HeartBeat[] heartBeats) {
        return Arrays.stream(heartBeats)
                .map(HeartBeat::toString)
                .reduce((a, b) -> String.format("%s\n%s", a, b))
                .orElse("");
    }

    private static String getHostNames(HeartBeat[] heartBeats) {
        return Arrays.stream(heartBeats)
                .map(HeartBeat::getHostId)
                .reduce((a, b) -> String.format("%s, %s", a, b))
                .orElse("");
    }

    private static boolean isEmpty(HeartBeat[] heartBeats) {
        return heartBeats == null || heartBeats.length == 0;
    }

    private static Notification[] NoNotifications() {
        return new Notification[0];
    }

}
