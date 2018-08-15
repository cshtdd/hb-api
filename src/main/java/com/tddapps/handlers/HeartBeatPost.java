package com.tddapps.handlers;

import com.tddapps.actions.HeartBeatPostAction;
import com.tddapps.controllers.HttpJsonController;
import com.tddapps.controllers.HttpJsonControllerDefault;
import com.tddapps.dal.DynamoDBMapperFactoryWithTablePrefix;
import com.tddapps.dal.HeartBeatRepositoryDynamo;
import com.tddapps.handlers.infrastructure.BaseHttpJsonHandler;

@SuppressWarnings("unused")
public class HeartBeatPost extends BaseHttpJsonHandler {
    @Override
    protected HttpJsonController getController() {
        HeartBeatRepositoryDynamo repository = new HeartBeatRepositoryDynamo(new DynamoDBMapperFactoryWithTablePrefix());
        HeartBeatPostAction action = new HeartBeatPostAction(repository);
        return new HttpJsonControllerDefault(action);
    }
}
