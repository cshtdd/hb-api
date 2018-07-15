package com.tddapps.controllers;

public class HttpJsonResponse {
    private final int statusCode;
    private final Object body;

    public HttpJsonResponse(int statusCode, Object body) {
        this.statusCode = statusCode;
        this.body = body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Object getBody() {
        return body;
    }
}
