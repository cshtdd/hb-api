package com.tddapps.controllers;

import com.fasterxml.jackson.databind.JsonNode;

import static com.tddapps.utils.StringExtensions.*;

public class HttpJsonActionStub implements HttpJsonAction<String, String>{
    private ActionBodyParseException seededParseException = null;
    private String seededParsedBody = null;
    private ActionProcessException seededProcessException = null;
    private int seededStatusCode = -1;
    private String seededResultBody = null;

    @Override
    public String parse(JsonNode body) throws ActionBodyParseException {
        if (seededParseException != null){
            throw seededParseException;
        }

        return seededParsedBody;
    }

    @Override
    public HttpJsonResponse<String> process(String body) throws ActionProcessException {
        if (seededProcessException != null){
            throw seededProcessException;
        }

        if (!EmptyWhenNull(seededParsedBody).equals(EmptyWhenNull(body))){
            throw new RuntimeException(String.format(
                    "Received unexpected process call. Expected: \"%s\", Actual: \"%s\"",
                    seededParsedBody,
                    body
            ));
        }

        return new HttpJsonResponse<>(seededStatusCode, seededResultBody);
    }

    public ActionBodyParseException getSeededParseException() {
        return seededParseException;
    }

    public void setSeededParseException(ActionBodyParseException seededParseException) {
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

    public ActionProcessException getSeededProcessException() {
        return seededProcessException;
    }

    public void setSeededProcessException(ActionProcessException seededProcessException) {
        this.seededProcessException = seededProcessException;
    }
}
