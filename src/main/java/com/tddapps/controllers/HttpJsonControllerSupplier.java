package com.tddapps.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class HttpJsonControllerSupplier implements HttpJsonController {
    private final HttpSupplierAction action;

    private static final Logger LOG = LogManager.getLogger(HttpJsonControllerSupplier.class);

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
            LOG.error("Action processing failed", e);
            return HttpJsonResponse.ServerErrorWithMessage(e.getMessage());
        }
    }
}
