package com.tddapps.model.aws;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.tddapps.model.DalException;
import com.tddapps.model.HeartBeat;
import com.tddapps.model.HeartBeatFactory;
import com.tddapps.model.HeartBeatListHelper;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static com.tddapps.utils.DateExtensions.UtcNow;
import static com.tddapps.utils.DateExtensions.UtcNowPlusMs;
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
        val seededHeartBeats = HeartBeatFactory.Create(10001);

        repository.Save(seededHeartBeats);

        HeartBeatListHelper.ShouldMatch(seededHeartBeats, repository.All());
    }

    @Test
    @Disabled
    public void CanRetrieveExpiredHeartBeatsOnly() throws DalException {
        val seededHeartBeats = HeartBeatFactory.CreateWithExpirations(
                UtcNowPlusMs(5000),
                UtcNowPlusMs(-15000),
                UtcNowPlusMs(6000),
                UtcNowPlusMs(-10000),
                UtcNowPlusMs(-20000)
        );
        repository.Save(seededHeartBeats);

        val expiredHeartBeats = repository.OlderThan(UtcNow());

        assertEquals(3, expiredHeartBeats.length);
    }
}
