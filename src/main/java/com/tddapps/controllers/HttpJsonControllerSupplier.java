package com.tddapps.controllers;

import lombok.extern.log4j.Log4j2;

import java.util.Map;

@Log4j2
public class HttpJsonControllerSupplier implements HttpJsonController {
    private final HttpSupplierAction action;

    public HttpSupplierAction getAction() {
        return action;
    }

    public HttpJsonControllerSupplier(HttpSupplierAction action) {
        this.action = action;
    }

    @Override
    public HttpJsonResponse process(Map<String, Object> input) {
        try {
            return getAction().process();
        } catch (ActionProcessException e) {
            log.error("Action processing failed", e);
            return HttpJsonResponse.ServerErrorWithMessage(e.getMessage());
        }
    }
}
