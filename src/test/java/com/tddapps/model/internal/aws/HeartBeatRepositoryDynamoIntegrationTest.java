package com.tddapps.model.internal.aws;

import cloud.localstack.docker.LocalstackDockerExtension;
import cloud.localstack.docker.annotation.LocalstackDockerProperties;
import com.tddapps.model.DalException;
import com.tddapps.model.heartbeats.HeartBeat;
import com.tddapps.model.heartbeats.test.HeartBeatFactory;
import com.tddapps.model.internal.aws.test.DynamoIntegrationTestHelper;
import com.tddapps.model.internal.aws.test.TestEnvironment;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.tddapps.model.heartbeats.test.HeartBeatFactory.TEST_REGION_DEFAULT;
import static com.tddapps.model.heartbeats.test.HeartBeatListTestHelper.ShouldMatch;
import static com.tddapps.utils.DateExtensions.EpochSecondsNow;
import static com.tddapps.utils.DateExtensions.ToReverseUtcMinuteString;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(LocalstackDockerExtension.class)
@LocalstackDockerProperties(services = { "dynamodb" }, environmentVariableProvider = TestEnvironment.class)
class HeartBeatRepositoryDynamoIntegrationTest {
    private HeartBeatRepositoryDynamo repository = null;

    @BeforeEach
    void Setup(){
        DynamoIntegrationTestHelper.ResetDatabase();

        val dbMapper = DynamoIntegrationTestHelper.createMapper();
        repository = new HeartBeatRepositoryDynamo(dbMapper);
    }

    @Test
    void ThereAreNoHeartBeatsByDefault() throws DalException {
        assertEquals(0, repository.All().length);

        val ttlNow = EpochSecondsNow();
        val minuteStringNow = ToReverseUtcMinuteString(ttlNow);
        assertEquals(0, repository.Read(minuteStringNow, 100).length);
    }

    @Test
    void MultipleHeartBeatsCanBeSavedInASingleOperation() throws DalException {
        val seededHeartBeats = HeartBeatFactory.Create(201);

        repository.Save(seededHeartBeats);

        ShouldMatch(seededHeartBeats, repository.All());
    }

    @Test
    void ReadsHeartBeatsByIds() throws DalException {
        val hostIds = IntStream.range(0, 100)
                .mapToObj(i -> String.format("test-host-%d", i + 100))
                .toArray(String[]::new);
        val seededHeartBeats = HeartBeatFactory.Create(201);
        repository.Save(seededHeartBeats);

        val heartBeats = repository.Read(hostIds);

        assertEquals(100, heartBeats.length);
    }

    @Test
    void ReadsHeartBeatsWithTTLInThePast() throws DalException {
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

        val actual = repository.Read(expirationMinuteUtcNow, 5);

        ShouldMatch(expected, actual);
    }

    @Test
    void CanDeleteHeartBeats() throws DalException {
        val seededHeartBeats = HeartBeatFactory.Create(201);
        repository.Save(seededHeartBeats);

        val DELETION_START = 50;
        val COUNT = 101;
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
        ShouldMatch(expectedRemainingList.toArray(new HeartBeat[0]), remainingHeartBeats);
    }
}
