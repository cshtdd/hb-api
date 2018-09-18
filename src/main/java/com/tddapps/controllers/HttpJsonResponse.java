package com.tddapps.controllers;

import com.tddapps.model.TextMessage;
import lombok.Data;

@Data
public class HttpJsonResponse<T> {
    private final int statusCode;
    private final T body;

    public static <T> HttpJsonResponse<T> Success(T body){
        return new HttpJsonResponse<>(200, body);
    }

    public static <T> HttpJsonResponse<T> BadRequest(T body) {
        return new HttpJsonResponse<>(400, body);
    }

    public static HttpJsonResponse<TextMessage> BadRequestWithMessage(String message){
        return BadRequest(TextMessage.create(message));
    }

    public static <T> HttpJsonResponse<T> ServerError(T body) {
        return new HttpJsonResponse<>(500, body);
    }

    public static HttpJsonResponse<TextMessage> ServerErrorWithMessage(String message){
        return ServerError(TextMessage.create(message));
    }

    @Override
    public String toString() {
        return String.format("%s(%d) %s", getClass().getSimpleName(), statusCode, body);
    }
}
