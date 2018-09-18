package com.tddapps.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.tddapps.actions.NotificationCalculatorAction;
import com.tddapps.controllers.ActionProcessException;
import com.tddapps.ioc.IocContainer;
import lombok.val;

@SuppressWarnings("unused")
public class NotificationCalculator implements RequestHandler<Boolean, Boolean> {
    private final NotificationCalculatorAction action;

    public NotificationCalculator(){
        this(IocContainer.getInstance().Resolve(NotificationCalculatorAction.class));
    }

    public NotificationCalculator(NotificationCalculatorAction action) {
        this.action = action;
    }

    @Override
    public Boolean handleRequest(Boolean input, Context context) {
        try {
            val process = action.process();
            return true;
        } catch (ActionProcessException e) {
            return false;
        }
    }
}
