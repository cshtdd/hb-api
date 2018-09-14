package com.tddapps.handlers;

import com.tddapps.actions.HeartBeatPostAction;
import com.tddapps.controllers.HttpJsonController;
import com.tddapps.controllers.HttpJsonControllerDefault;
import com.tddapps.handlers.infrastructure.BaseHttpJsonHandler;
import com.tddapps.ioc.IocContainer;
import lombok.val;

@SuppressWarnings("unused")
public class HeartBeatPost extends BaseHttpJsonHandler {
    @Override
    protected HttpJsonController getController() {
        val action = IocContainer.getInstance().Resolve(HeartBeatPostAction.class);
        return new HttpJsonControllerDefault<>(action);
    }
}
