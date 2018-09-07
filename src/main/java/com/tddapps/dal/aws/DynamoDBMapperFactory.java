package com.tddapps.dal.aws;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

public interface DynamoDBMapperFactory {
    DynamoDBMapper getMapper();
}
