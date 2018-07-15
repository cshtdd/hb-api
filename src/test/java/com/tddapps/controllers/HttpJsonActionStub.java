package com.tddapps.controllers;

import com.fasterxml.jackson.databind.JsonNode;

import static com.tddapps.utils.StringExtensions.*;

public class HttpJsonActionStub implements HttpJsonAction<String, String>{
    private BodyParseException seededParseException = null;
    private String seededParsedBody = null;
    private int seededStatusCode = -1;
    private String seededResultBody = null;

    @Override
    public String parse(JsonNode body) throws BodyParseException {
        if (seededParseException != null){
            throw seededParseException;
        }

        return seededParsedBody;
    }

    @Override
    public HttpJsonResponse<String> process(String body) {
        if (!EmptyWhenNull(seededParsedBody).equals(EmptyWhenNull(body))){
            throw new RuntimeException(String.format(
                    "Received unexpected process call. Expected: \"%s\", Actual: \"%s\"",
                    seededParsedBody,
                    body
            ));
        }

        return new HttpJsonResponse<>(seededStatusCode, seededResultBody);
    }

    public BodyParseException getSeededParseException() {
        return seededParseException;
    }

    public void setSeededParseException(BodyParseException seededParseException) {
        this.seededParseException = seededParseException;
    }

    public String getSeededParsedBody() {
        return seededParsedBody;
    }

    public void setSeededParsedBody(String seededParsedBody) {
        this.seededParsedBody = seededParsedBody;
    }

    public String getSeededResultBody() {
        return seededResultBody;
    }

    public void setSeededResultBody(String seededResultBody) {
        this.seededResultBody = seededResultBody;
    }

    public int getSeededStatusCode() {
        return seededStatusCode;
    }

    public void setSeededStatusCode(int seededStatusCode) {
        this.seededStatusCode = seededStatusCode;
    }
}
