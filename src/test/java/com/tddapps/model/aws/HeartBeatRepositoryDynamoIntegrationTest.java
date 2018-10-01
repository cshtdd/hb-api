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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import static com.tddapps.model.HeartBeatFactory.TEST_REGION_DEFAULT;
import static com.tddapps.utils.DateExtensions.EpochSecondsNow;
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

        val ttlNow = EpochSecondsNow();
        val minuteStringNow = ToReverseUtcMinuteString(ttlNow);
        assertEquals(0, repository.Read(minuteStringNow, ttlNow, 100).length);
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
                new HeartBeat("host-00", ttlOneSecondAgo, TEST_REGION_DEFAULT, false),
                new HeartBeat("host-01", ttlOneSecondAgo - 1, "us-test-2", false),
                new HeartBeat("host-02", ttlOneSecondAgo - 2, "us-test-2", false),
                new HeartBeat("host-03", ttlOneSecondAgo - 3, "us-test-2", false),
                new HeartBeat("host-04", ttlOneSecondAgo - 4, TEST_REGION_DEFAULT, false),
                new HeartBeat("host-05", ttlOneSecondAgo - 5, TEST_REGION_DEFAULT, false),
                new HeartBeat("host-06", ttlFuture, TEST_REGION_DEFAULT, false),
                new HeartBeat("host-07", ttlFuture, TEST_REGION_DEFAULT, false),
                new HeartBeat("host-08", ttlTenSecondsAgo, TEST_REGION_DEFAULT, false),
                new HeartBeat("host-09", ttlTenSecondsAgo - 1, TEST_REGION_DEFAULT, false),
                new HeartBeat("host-10", ttlTenSecondsAgo - 2, TEST_REGION_DEFAULT, false),
                new HeartBeat("host-11", ttlTenSecondsAgo - 3, "us-test-2", false),
                new HeartBeat("host-12", ttlTenSecondsAgo - 4, "us-test-2", false),
                new HeartBeat("host-13", ttlTenSecondsAgo - 5, TEST_REGION_DEFAULT, false),
                new HeartBeat("host-14", ttlMoreThanOneMinuteAgo, TEST_REGION_DEFAULT, false),
                new HeartBeat("host-15", ttlMoreThanOneMinuteAgo, TEST_REGION_DEFAULT, false),
                new HeartBeat("host-16", ttlMoreThanOneMinuteAgo, TEST_REGION_DEFAULT, false),
                new HeartBeat("host-17", ttlMoreThanOneMinuteAgo, TEST_REGION_DEFAULT, false),
                new HeartBeat("host-18", ttlMoreThanOneMinuteAgo, TEST_REGION_DEFAULT, false),
        };
        val expected = new HeartBeat[]{
                seededHeartBeats[9],
                seededHeartBeats[10],
                seededHeartBeats[11],
                seededHeartBeats[12],
                seededHeartBeats[13],
        };
        repository.Save(seededHeartBeats);

        val actual = repository.Read(expirationMinuteUtcNow, ttlNow, 5);

        HeartBeatListHelper.ShouldMatch(expected, actual);
    }

    @Test
    public void CanDeleteHeartBeats() throws DalException {
        val seededHeartBeats = HeartBeatFactory.Create(2001);
        repository.Save(seededHeartBeats);

        val DELETION_START = 500;
        val COUNT = 1001;
        val heartBeatsToDelete = Arrays.stream(seededHeartBeats, DELETION_START, DELETION_START + COUNT)
                .toArray(HeartBeat[]::new);
        val expectedRemainingList = new ArrayList<HeartBeat>();
        expectedRemainingList.addAll(
                Arrays.stream(seededHeartBeats, 0, DELETION_START)
                        .collect(Collectors.toList())
        );
        expectedRemainingList.addAll(
                Arrays.stream(seededHeartBeats, DELETION_START + COUNT, seededHeartBeats.length)
                        .collect(Collectors.toList())
        );

        repository.Delete(heartBeatsToDelete);

        val remainingHeartBeats = repository.All();
        assertEquals(seededHeartBeats.length - heartBeatsToDelete.length, remainingHeartBeats.length);
        HeartBeatListHelper.ShouldMatch(expectedRemainingList.toArray(new HeartBeat[0]), remainingHeartBeats);
    }
}
