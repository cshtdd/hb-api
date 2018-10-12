package com.tddapps.model.aws;

import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;

import java.util.List;

public interface DynamoDBEventParser {
    <T> List<T> readDeletions(DynamodbEvent input, Class<T> clazz);
    <T> List<T> readInsertions(DynamodbEvent input, Class<T> clazz);
}
