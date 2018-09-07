package com.tddapps.dal;

public class SingleNotificationBuilder implements HeartBeatNotificationBuilder {
    @Override
    public Notification[] build(HeartBeat[] heartBeats) {
        if (isEmpty(heartBeats)){
            return NoNotifications();
        }

        String subject = String.format("Hosts missing [%s]", heartBeats[0].getHostId());

        String message = String.format("%s\n\n%s\n--", subject, heartBeats[0]);

        return new Notification[]{
                new Notification(subject, message)
        };
    }

    private boolean isEmpty(HeartBeat[] heartBeats) {
        return heartBeats == null || heartBeats.length == 0;
    }

    private Notification[] NoNotifications() {
        return new Notification[0];
    }
}
