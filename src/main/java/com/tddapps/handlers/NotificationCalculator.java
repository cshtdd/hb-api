package com.tddapps.handlers;

import com.tddapps.actions.NotificationCalculatorAction;
import com.tddapps.controllers.HttpJsonController;
import com.tddapps.controllers.HttpJsonControllerSupplier;
import com.tddapps.handlers.infrastructure.BaseHttpJsonHandler;
import com.tddapps.ioc.IocContainer;

@SuppressWarnings("unused")
public class NotificationCalculator extends BaseHttpJsonHandler {
    @Override
    protected HttpJsonController getController() {
        NotificationCalculatorAction action = IocContainer.getInstance().Resolve(NotificationCalculatorAction.class);
        return new HttpJsonControllerSupplier(action);
    }
}
