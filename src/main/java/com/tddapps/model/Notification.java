package com.tddapps.model;

import lombok.Data;
import lombok.NonNull;

@Data
public class Notification {
    @NonNull
    private final String subject;
    @NonNull
    private final String message;

    @Override
    public String toString() {
        return String.format(
                "%s, subject: %s, message: %s",
                getClass().getSimpleName(),
                subject,
                message
        );
    }
}
