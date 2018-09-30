package com.tddapps.model.aws;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.tddapps.model.DalException;
import com.tddapps.model.HeartBeat;
import com.tddapps.model.HeartBeatFactory;
import com.tddapps.model.HeartBeatListHelper;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static com.tddapps.utils.DateExtensions.ToReverseUtcMinuteString;
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
        val seededHeartBeats = HeartBeatFactory.Create(20001);

        repository.Save(seededHeartBeats);

        HeartBeatListHelper.ShouldMatch(seededHeartBeats, repository.All());
    }


    @Test
    public void ReadsHeartBeatsWithTTLInThePast() throws DalException {
        // 2097-07-17T20:05:31Z[UTC]
        val seededDate = ZonedDateTime.of(
                2097, 7, 17,
                20, 5, 31,
                45000000,
                ZoneId.of("UTC")
        );

        val ttlNow = seededDate.toInstant().getEpochSecond();
        val expirationMinuteUtcNow = ToReverseUtcMinuteString(ttlNow);
        val ttlOneSecondAgo = ttlNow - 1;
        val ttlTenSecondsAgo = ttlNow - 10;
        val ttlMoreThanOneMinuteAgo = ttlNow - 80;
        val ttlFuture = ttlNow + 1;
        val seededHeartBeats = new HeartBeat[]{
                new HeartBeat("host-00", ttlOneSecondAgo, "us-test-1", false),
                new HeartBeat("host-01", ttlOneSecondAgo - 1, "us-test-2", false),
                new HeartBeat("host-02", ttlOneSecondAgo - 2, "us-test-2", false),
                new HeartBeat("host-03", ttlOneSecondAgo - 3, "us-test-2", false),
                new HeartBeat("host-04", ttlOneSecondAgo - 4, "us-test-1", false),
                new HeartBeat("host-05", ttlOneSecondAgo - 5, "us-test-1", false),
                new HeartBeat("host-06", ttlFuture, "us-test-1", false),
                new HeartBeat("host-07", ttlFuture, "us-test-1", false),
                new HeartBeat("host-08", ttlTenSecondsAgo, "us-test-1", false),
                new HeartBeat("host-09", ttlTenSecondsAgo - 1, "us-test-1", false),
                new HeartBeat("host-10", ttlTenSecondsAgo - 2, "us-test-1", false),
                new HeartBeat("host-11", ttlTenSecondsAgo - 3, "us-test-2", false),
                new HeartBeat("host-12", ttlTenSecondsAgo - 4, "us-test-2", false),
                new HeartBeat("host-13", ttlTenSecondsAgo - 5, "us-test-1", false),
                new HeartBeat("host-14", ttlMoreThanOneMinuteAgo, "us-test-1", false),
                new HeartBeat("host-15", ttlMoreThanOneMinuteAgo, "us-test-1", false),
                new HeartBeat("host-16", ttlMoreThanOneMinuteAgo, "us-test-1", false),
                new HeartBeat("host-17", ttlMoreThanOneMinuteAgo, "us-test-1", false),
                new HeartBeat("host-18", ttlMoreThanOneMinuteAgo, "us-test-1", false),
        };
        val expected = new HeartBeat[]{
                seededHeartBeats[9],
                seededHeartBeats[10],
                seededHeartBeats[11],
                seededHeartBeats[12],
                seededHeartBeats[13],
        };
        repository.Save(seededHeartBeats);

        val actual = repository.ReadOlderThan(expirationMinuteUtcNow, ttlNow, 5);

        HeartBeatListHelper.ShouldMatch(expected, actual);
    }

}
