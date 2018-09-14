package com.tddapps.model.aws;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.tddapps.model.DalException;
import com.tddapps.model.HeartBeatFactory;
import com.tddapps.model.HeartBeatListHelper;
import lombok.val;
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

    @Test
    public void HeartBeatsCanBeSaved() throws DalException {
        val seededHeartBeats = HeartBeatFactory.Create(1000);
        for (val seededHeartBeat : seededHeartBeats) {
            repository.Save(seededHeartBeat);
        }

        HeartBeatListHelper.ShouldMatch(seededHeartBeats, repository.All());
    }

    @Test
    public void MultipleHeartBeatsCanBeSavedInASingleOperation() throws DalException {
        val seededHeartBeats = HeartBeatFactory.Create(100001);

        repository.Save(seededHeartBeats);

        HeartBeatListHelper.ShouldMatch(seededHeartBeats, repository.All());
    }
}
