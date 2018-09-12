package com.tddapps.model.aws;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.tddapps.model.DalException;
import com.tddapps.model.HeartBeatFactory;
import lombok.val;
import lombok.var;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

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
        var seededHeartBeats = HeartBeatFactory.create(1000);
        for (val seededHeartBeat : seededHeartBeats) {
            repository.Save(seededHeartBeat);
        }

        val heartBeats = repository.All();

        assertEquals(1000, heartBeats.length);

        val allSeededHeartBeatsHaveBeenRetrieved = Arrays.stream(heartBeats)
                .allMatch(hb -> Arrays.stream(seededHeartBeats)
                        .anyMatch(hb2 -> hb.almostEquals(hb2))
                );
        assertTrue(allSeededHeartBeatsHaveBeenRetrieved);
    }
}
