package com.tddapps.dal;

public class SingleNotificationBuilder implements HeartBeatNotificationBuilder {
    @Override
    public Notification[] build(HeartBeat[] heartBeats) {
        if (isEmpty(heartBeats)){
            return NoNotifications();
        }

        String subject = String.format("Hosts missing [%s]", heartBeats[0].getHostId());

        return new Notification[]{
                new Notification(subject, null)
        };
    }

    private boolean isEmpty(HeartBeat[] heartBeats) {
        return heartBeats == null || heartBeats.length == 0;
    }

    private Notification[] NoNotifications() {
        return new Notification[0];
    }
}
