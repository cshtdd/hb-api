package com.tddapps.model.aws;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.tddapps.model.HeartBeat;
import com.tddapps.model.Settings;
import com.tddapps.model.SettingsReader;
import lombok.val;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DynamoHelperIntegrationTests {
    public static final String HEARTBEATS_TABLE_NAME = "dev-int-heartbeats";

    public static DynamoDBMapper createMapper(){
        val settingsReader = mock(SettingsReader.class);
        when(settingsReader.ReadString(Settings.TABLE_PREFIX))
                .thenReturn("dev-int-");

        return new DynamoDBMapperFactory()
                .createMapper(settingsReader, createClient());
    }

    public static AmazonDynamoDB createClient(){
        val settingsReader = mock(SettingsReader.class);
        when(settingsReader.ReadString(Settings.DYNAMO_DB_ENDPOINT_OVERRIDE))
                .thenReturn("http://localhost:8001");

        return new AmazonDynamoDBFactory()
                .createClient(settingsReader);
    }

    public static void ResetDatabase(){
        val mapper = createMapper();
        val client = createClient();

        CreateEmptyTable(HEARTBEATS_TABLE_NAME, HeartBeat.class, mapper, client);
    }

    public static void CreateEmptyTable(String tableName, Class<?> clazz){
        CreateEmptyTable(tableName, clazz, createMapper(), createClient());
    }

    public static void CreateEmptyTable(String tableName, Class<?> clazz, DynamoDBMapper dbMapper, AmazonDynamoDB client) {
        val heartBeatsTableExists = client.listTables()
                .getTableNames()
                .contains(tableName);
        if (heartBeatsTableExists) {
            client.deleteTable(tableName);
        }

        val throughput = new ProvisionedThroughput(1L, 1L);
        val createTableRequest = dbMapper.generateCreateTableRequest(clazz)
                .withProvisionedThroughput(throughput);
        client.createTable(createTableRequest);
    }
}
