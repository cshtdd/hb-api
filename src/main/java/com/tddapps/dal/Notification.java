package com.tddapps.dal;

public class Notification {
    private final String subject;
    private final String message;

    public Notification(String subject, String message) {
        this.subject = subject;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getSubject() {
        return subject;
    }
}
