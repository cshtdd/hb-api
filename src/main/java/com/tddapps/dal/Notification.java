package com.tddapps.dal;

import java.util.Objects;

import static com.tddapps.utils.StringExtensions.EmptyWhenNull;

public class Notification {
    private final String subject;
    private final String message;

    public Notification(String subject, String message) {
        this.subject = EmptyWhenNull(subject);
        this.message = EmptyWhenNull(message);
    }

    public String getMessage() {
        return message;
    }

    public String getSubject() {
        return subject;
    }

    @Override
    public int hashCode() {
        return Objects.hash(subject, message);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Notification)){
            return false;
        }

        Notification that = (Notification)obj;

        return this.subject.equals(that.subject) &&
                this.message.equals(that.message);
    }
}
