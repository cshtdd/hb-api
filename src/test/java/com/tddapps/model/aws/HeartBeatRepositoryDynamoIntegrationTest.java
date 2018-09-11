package com.tddapps.model.aws;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.tddapps.model.DalException;
import com.tddapps.model.HeartBeat;
import lombok.val;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HeartBeatRepositoryDynamoIntegrationTest {
    private DynamoDBMapper dbMapper = null;
    private HeartBeatRepositoryDynamo repository = null;

    @BeforeEach
    public void Setup(){
        dbMapper = DynamoHelperIntegrationTests.createMapper();

        repository = new HeartBeatRepositoryDynamo(dbMapper);
    }

    @Test
    public void ResetsTheDatabase(){
        val client = DynamoHelperIntegrationTests.createClient();

        val heartBeatsTableExists = client.listTables()
                .getTableNames()
                .contains(DynamoHelperIntegrationTests.HEARTBEATS_TABLE_NAME);
        if (heartBeatsTableExists) {
            client.deleteTable(DynamoHelperIntegrationTests.HEARTBEATS_TABLE_NAME);
        }

        val throughput = new ProvisionedThroughput(1L, 1L);
        val createTableRequest = dbMapper.generateCreateTableRequest(HeartBeat.class)
                .withProvisionedThroughput(throughput);
        client.createTable(createTableRequest);
    }

    @Test
    public void SampleTest(){
        assertTrue(true);
    }

    @Test
    public void ThereAreNoHeartBeatsByDefault() throws DalException {
        assertEquals(0, repository.All().length);
    }
}
