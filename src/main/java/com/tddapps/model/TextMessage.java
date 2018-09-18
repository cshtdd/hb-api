package com.tddapps.model;

import lombok.Data;
import lombok.NonNull;

import static com.tddapps.utils.StringExtensions.EmptyWhenNull;

@Data(staticConstructor = "create")
public class TextMessage {
    @NonNull
    private final String message;

    public static final TextMessage OK = TextMessage.create("OK");

    public String asJson() {
        return String.format("{\"message\": \"%s\"}", EmptyWhenNull(message));
    }

    @Override
    public String toString() {
        return String.format("%s:%s", getClass().getSimpleName(), EmptyWhenNull(message));
    }
}
