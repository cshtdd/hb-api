package com.tddapps.model.aws;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.tddapps.model.DalException;
import com.tddapps.model.HeartBeatFactory;
import lombok.var;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    public void HeartBeatsCanBeSaved() throws DalException {
        var hb1 = HeartBeatFactory.create();
        repository.Save(hb1);

        var heartBeats = repository.All();

        assertEquals(1, heartBeats.length);
        assertTrue(hb1.almostEquals(heartBeats[0]));
    }
}
