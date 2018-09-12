package com.tddapps.model.aws;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HeartBeatRepositoryDynamoTest {
    @Test
    public void HasAMaximumBatchSizeOfTwentyFive(){
        assertEquals(25, HeartBeatRepositoryDynamo.DYNAMO_MAX_BATCH_SIZE);
    }
}
