package com.tddapps.model.aws;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.tddapps.model.Settings;
import com.tddapps.model.SettingsReader;

public class DynamoDBMapperFactoryWithTablePrefix implements DynamoDBMapperFactory {
    private final DynamoDBMapper mapper;

    public DynamoDBMapperFactoryWithTablePrefix(SettingsReader settingsReader){
        String tablePrefix = settingsReader.ReadString(Settings.TABLE_PREFIX);

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
        DynamoDBMapperConfig.TableNameOverride tableNameOverride =
                DynamoDBMapperConfig.TableNameOverride.withTableNamePrefix(tablePrefix);

        DynamoDBMapperConfig.Builder configBuilder = DynamoDBMapperConfig.builder();
        configBuilder.setTableNameOverride(tableNameOverride);
        DynamoDBMapperConfig config = configBuilder.build();

        mapper = new DynamoDBMapper(client, config);
    }

    @Override
    public DynamoDBMapper getMapper() {
        return mapper;
    }
}
