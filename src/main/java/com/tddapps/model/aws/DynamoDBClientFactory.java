package com.tddapps.model.aws;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;

public interface DynamoDBClientFactory {
    AmazonDynamoDB getClient();
}
