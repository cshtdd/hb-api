package com.tddapps.handlers;

import com.tddapps.actions.StatusGetAction;
import com.tddapps.controllers.HttpJsonController;
import com.tddapps.controllers.HttpJsonControllerSupplier;
import com.tddapps.handlers.infrastructure.BaseHttpJsonHandler;

@SuppressWarnings("unused")
public class StatusGet extends BaseHttpJsonHandler {
    @Override
    protected HttpJsonController getController() {
        return new HttpJsonControllerSupplier(new StatusGetAction());
    }
}
