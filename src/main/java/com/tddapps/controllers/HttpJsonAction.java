package com.tddapps.controllers;

import com.fasterxml.jackson.databind.JsonNode;

public interface HttpJsonAction<T> {
    T parse(JsonNode body) throws BodyParseException;
    HttpJsonResponse process(T body);
}

