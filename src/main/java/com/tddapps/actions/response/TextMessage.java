package com.tddapps.actions.response;

public class TextMessage {
    private final String message;

    public TextMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String asJson() {
        String sanitizedMessage = message == null ? "" : message;

        return String.format("{\"message\": \"%s\"}", sanitizedMessage);
    }
}
