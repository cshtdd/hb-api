package com.tddapps.dal;

public class Notification {
    private final String message;
    private final String subject;

    public Notification(String message, String subject) {
        this.message = message;
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public String getSubject() {
        return subject;
    }
}
