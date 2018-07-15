package com.tddapps.actions.response;

import static com.tddapps.utils.StringExtensions.*;

public class TextMessage {
    private final String message;

    public TextMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String asJson() {
        return String.format("{\"message\": \"%s\"}", EmptyWhenNull(message));
    }
}
