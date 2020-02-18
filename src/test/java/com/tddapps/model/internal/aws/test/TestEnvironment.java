package com.tddapps.model.internal.aws.test;

import cloud.localstack.docker.annotation.IEnvironmentVariableProvider;

import java.util.HashMap;
import java.util.Map;

public class TestEnvironment implements IEnvironmentVariableProvider {
    @Override
    public Map<String, String> getEnvironmentVariables() {
        return new HashMap<String, String>(){{
            put("DEFAULT_REGION", "test_sandbox");
        }};
    }
}
