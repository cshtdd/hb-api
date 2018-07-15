package com.tddapps.handlers;

import com.tddapps.controllers.HttpJsonController;

public class HttpJsonHandlerStub extends BaseHttpJsonHandler {
    private final HttpJsonController seededController;

    public HttpJsonHandlerStub(HttpJsonController seededController) {
        this.seededController = seededController;
    }

    @Override
    protected HttpJsonController getController() {
        return seededController;
    }
}
