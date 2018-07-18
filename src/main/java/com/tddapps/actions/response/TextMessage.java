package com.tddapps.actions.response;

import java.util.Objects;

import static com.tddapps.utils.StringExtensions.*;

public class TextMessage {
    private final String message;

    public static final TextMessage OK = TextMessage.create("OK");

    public static TextMessage create(String message){
        return new TextMessage(message);
    }

    private TextMessage(String message) {
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
        return String.format("%s:%s", getClass().getSimpleName(), EmptyWhenNull(message));
    }
}
