package com.tddapps.handlers;

import com.tddapps.actions.HeartBeatPostAction;
import com.tddapps.controllers.HttpJsonController;
import com.tddapps.controllers.HttpJsonControllerDefault;
import com.tddapps.dal.HeartBeatRepositoryDynamo;
import com.tddapps.handlers.infrastructure.BaseHttpJsonHandler;

@SuppressWarnings("unused")
public class HeartBeatPost extends BaseHttpJsonHandler {
    @Override
    protected HttpJsonController getController() {
        HeartBeatPostAction action = new HeartBeatPostAction(new HeartBeatRepositoryDynamo());
        return new HttpJsonControllerDefault(action);
    }
}
