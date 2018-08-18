package com.tddapps.handlers;

import com.tddapps.actions.NotificationCalculatorAction;
import com.tddapps.controllers.HttpJsonControllerSupplier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class NotificationCalculatorTest {
    @Test
    public void BuildsTheCorrectController(){
        HttpJsonControllerSupplier controller = (HttpJsonControllerSupplier) new NotificationCalculator().getController();
        assertTrue(controller.getAction() instanceof NotificationCalculatorAction);
    }
}