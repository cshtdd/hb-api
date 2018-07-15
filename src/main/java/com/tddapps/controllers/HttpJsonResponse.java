package com.tddapps.controllers;

public class HttpJsonResponse<T> {
    private final int statusCode;
    private final T body;

    public HttpJsonResponse(int statusCode, T body) {
        this.statusCode = statusCode;
        this.body = body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public T getBody() {
        return body;
    }
}
