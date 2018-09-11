package com.tddapps.model.aws;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.tddapps.model.DalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HeartBeatRepositoryDynamoIntegrationTest {
    private DynamoDBMapper dbMapper = null;
    private HeartBeatRepositoryDynamo repository = null;

    @BeforeEach
    public void Setup(){
        DynamoHelperIntegrationTests.ResetDatabase();

        dbMapper = DynamoHelperIntegrationTests.createMapper();
        repository = new HeartBeatRepositoryDynamo(dbMapper);
    }

    @Test
    public void ThereAreNoHeartBeatsByDefault() throws DalException {
        assertEquals(0, repository.All().length);
    }
}
