package com.tddapps.actions.response;

import java.util.Objects;

import static com.tddapps.utils.StringExtensions.*;

public class TextMessage {
    private final String message;

    public static final TextMessage OK = new TextMessage("OK");

    public TextMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String asJson() {
        return String.format("{\"message\": \"%s\"}", EmptyWhenNull(message));
    }

    @Override
    public int hashCode() {
        return Objects.hash(message);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TextMessage)){
            return false;
        }

        return this.toString().equals(obj.toString());
    }

    @Override
    public String toString() {
        return String.format("TextMessage:%s", EmptyWhenNull(message));
    }
}
