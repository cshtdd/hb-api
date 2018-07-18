package com.tddapps.handlers;

import com.tddapps.actions.HeartBeatPostAction;
import com.tddapps.controllers.HttpJsonControllerDefault;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HeartBeatPostTest {
    @Test
    public void BuildsTheCorrectController(){
        HttpJsonControllerDefault controller = (HttpJsonControllerDefault)new HeartBeatPost().getController();
        assertTrue(controller.getAction() instanceof HeartBeatPostAction);
    }
}
