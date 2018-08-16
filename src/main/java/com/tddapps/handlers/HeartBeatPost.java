package com.tddapps.handlers;

import com.tddapps.actions.HeartBeatPostAction;
import com.tddapps.controllers.HttpJsonController;
import com.tddapps.controllers.HttpJsonControllerDefault;
import com.tddapps.dal.DynamoDBMapperFactoryWithTablePrefix;
import com.tddapps.dal.HeartBeatRepositoryDynamo;
import com.tddapps.handlers.infrastructure.BaseHttpJsonHandler;
import com.tddapps.ioc.IocContainer;

@SuppressWarnings("unused")
public class HeartBeatPost extends BaseHttpJsonHandler {
    @Override
    protected HttpJsonController getController() {
        HeartBeatPostAction action = IocContainer.getInstance().Resolve(HeartBeatPostAction.class);
        return new HttpJsonControllerDefault(action);
    }
}
