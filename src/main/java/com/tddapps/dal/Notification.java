package com.tddapps.dal;

import java.util.Objects;

import static com.tddapps.utils.StringExtensions.EmptyWhenNull;

public class Notification implements Cloneable {
    private String subject;
    private String message;

    protected Notification(Notification that) {
        this(that.subject, that.message);
    }

    public Notification(String subject, String message) {
        setSubject(subject);
        setMessage(message);
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = EmptyWhenNull(subject);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = EmptyWhenNull(message);
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

    @Override
    public String toString() {
        return String.format(
                "%s, subject: %s, message: %s",
                getClass().getSimpleName(),
                subject,
                message
        );
    }

    public Object clone(){
        return new Notification(this);
    }
}
