package com.tddapps.handlers;

import com.tddapps.actions.HeartBeatPostAction;
import com.tddapps.controllers.HttpJsonController;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HeartBeatPostTest {
    @Test
    public void BuildsTheCorrectController(){
        HttpJsonController controller = new HeartBeatPost().getController();
        assertTrue(controller.getAction() instanceof HeartBeatPostAction);
    }
}
