package com.tddapps.model.internal.aws.test;

import cloud.localstack.docker.annotation.IEnvironmentVariableProvider;

import java.util.HashMap;
import java.util.Map;

public class TestEnvironment implements IEnvironmentVariableProvider {
    public final static String ENDPOINT_URL_SQS = "http://localhost:4576";
    public final static String DEFAULT_REGION = "test_sandbox";

    @Override
    public Map<String, String> getEnvironmentVariables() {
        return new HashMap<String, String>(){{
            put("DEFAULT_REGION", DEFAULT_REGION);
        }};
    }
}
