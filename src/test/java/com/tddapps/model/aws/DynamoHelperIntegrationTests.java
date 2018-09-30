package com.tddapps.model.aws;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.*;
import com.tddapps.model.HeartBeat;
import com.tddapps.model.Settings;
import com.tddapps.model.SettingsReader;
import lombok.val;

import java.util.ArrayList;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class DynamoHelperIntegrationTests {
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

        CreateEmptyHeartBeatsTable(mapper, client);
    }

    public static void CreateEmptyHeartBeatsTable(DynamoDBMapper dbMapper, AmazonDynamoDB client) {
        val heartBeatsTableExists = client.listTables()
                .getTableNames()
                .contains(HEARTBEATS_TABLE_NAME);
        if (heartBeatsTableExists) {
            client.deleteTable(HEARTBEATS_TABLE_NAME);
        }

        val createTableRequest = dbMapper.generateCreateTableRequest(HeartBeat.class)
                .withAttributeDefinitions(new ArrayList<AttributeDefinition>(){{
                    add(new AttributeDefinition("host_id", "S"));
                    add(new AttributeDefinition("ttl", "N"));
                    add(new AttributeDefinition("expiration_minute_utc", "S"));
                }})
                .withProvisionedThroughput(new ProvisionedThroughput(1L, 1L))
                .withGlobalSecondaryIndexes(new ArrayList<GlobalSecondaryIndex>(){{
                    add(new GlobalSecondaryIndex()
                            .withIndexName("ExpirationMinuteIndex")
                            .withKeySchema(new ArrayList<KeySchemaElement>(){{
                                add(new KeySchemaElement("expiration_minute_utc", "HASH"));
                                add(new KeySchemaElement("ttl", "RANGE"));
                            }})
                            .withProjection(new Projection().withProjectionType("ALL"))
                            .withProvisionedThroughput(new ProvisionedThroughput(1L, 1L))
                    );
                }});
        client.createTable(createTableRequest);
    }
}
