package com.tddapps.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class HeartBeatExpirator implements RequestHandler<String, Boolean> {
    @Override
    public Boolean handleRequest(String input, Context context) {
        return null;
    }
}
