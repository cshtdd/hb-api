package com.tddapps.handlers;

import com.tddapps.actions.StatusGetAction;
import com.tddapps.controllers.HttpJsonControllerSupplier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StatusGetTest {
    @Test
    public void BuildsTheCorrectController(){
        HttpJsonControllerSupplier controller = (HttpJsonControllerSupplier) new StatusGet().getController();
        assertTrue(controller.getAction() instanceof StatusGetAction);
    }
}
