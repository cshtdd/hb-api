package com.tddapps.model.internal.aws;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HeartBeatRepositoryDynamoTest {
    @Test
    public void HasAMaximumBatchSizeOfTwentyFive(){
        Assertions.assertEquals(25, HeartBeatRepositoryDynamo.DYNAMO_MAX_BATCH_SIZE);
    }
}
