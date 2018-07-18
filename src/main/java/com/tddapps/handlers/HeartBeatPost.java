package com.tddapps.handlers;

import com.tddapps.actions.HeartBeatPostAction;
import com.tddapps.controllers.HttpJsonController;
import com.tddapps.controllers.HttpJsonControllerDefault;

@SuppressWarnings("unused")
public class HeartBeatPost extends BaseHttpJsonHandler {
    @Override
    protected HttpJsonController getController() {
        return new HttpJsonControllerDefault(new HeartBeatPostAction());
    }
}
