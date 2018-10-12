package com.tddapps.model;

import lombok.Data;
import lombok.NonNull;

@Data
public class NotificationMetadata {
    @NonNull
    private final String subject;

    @Override
    public String toString(){
        return String.format(
                "%s, subject: %s",
                getClass().getSimpleName(),
                subject
        );
    }
}
