package com.tddapps.model.aws;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
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
}
