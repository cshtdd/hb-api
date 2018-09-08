package com.tddapps.controllers;

import com.tddapps.actions.response.TextMessage;
import lombok.Data;

@Data
public class HttpJsonResponse<T> {
    private final int statusCode;
    private final T body;

    public static <T> HttpJsonResponse Success(T body){
        return new HttpJsonResponse<>(200, body);
    }

    public static <T> HttpJsonResponse BadRequest(T body) {
        return new HttpJsonResponse<>(400, body);
    }

    public static HttpJsonResponse BadRequestWithMessage(String message){
        return BadRequest(TextMessage.create(message));
    }

    public static <T> HttpJsonResponse ServerError(T body) {
        return new HttpJsonResponse<>(500, body);
    }

    public static HttpJsonResponse ServerErrorWithMessage(String message){
        return ServerError(TextMessage.create(message));
    }

    @Override
    public String toString() {
        return String.format("%s(%d) %s", getClass().getSimpleName(), statusCode, body);
    }
}
