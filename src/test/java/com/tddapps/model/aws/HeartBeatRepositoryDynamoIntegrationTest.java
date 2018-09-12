package com.tddapps.model.aws;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.tddapps.model.DalException;
import com.tddapps.model.HeartBeat;
import com.tddapps.model.HeartBeatFactory;
import lombok.val;
import lombok.var;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.function.Predicate;

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

        listsMatch(seededHeartBeats, repository.All());
    }

    @Test
    public void MultipleHeartBeatsCanBeSavedInASingleOperation() throws DalException {
        var seededHeartBeats = HeartBeatFactory.create(10001);

        repository.Save(seededHeartBeats);

        listsMatch(seededHeartBeats, repository.All());
    }

    private void listsMatch(HeartBeat[] listA, HeartBeat[] listB) {
        assertEquals(listA.length, listB.length);

        Predicate<HeartBeat> listBContainsHeartBeat = hb -> Arrays.stream(listB)
                .anyMatch(hb::almostEquals);

        val allSeededHeartBeatsHaveBeenRetrieved = Arrays.stream(listA)
                .allMatch(listBContainsHeartBeat);

        assertTrue(allSeededHeartBeatsHaveBeenRetrieved);
    }
}
