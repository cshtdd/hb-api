package com.tddapps.handlers;

import com.tddapps.actions.HeartBeatPostAction;
import com.tddapps.controllers.HttpJsonControllerDefault;
import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HeartBeatPostTest {
    @Test
    public void BuildsTheCorrectController(){
        val controller = (HttpJsonControllerDefault)new HeartBeatPost().getController();
        assertTrue(controller.getAction() instanceof HeartBeatPostAction);
    }
}
