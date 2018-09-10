package com.tddapps.model.aws;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;

public class DynamoDBClientFactoryDefault implements DynamoDBClientFactory {
    @Override
    public AmazonDynamoDB getClient() {
        return AmazonDynamoDBClientBuilder.defaultClient();
    }
}
