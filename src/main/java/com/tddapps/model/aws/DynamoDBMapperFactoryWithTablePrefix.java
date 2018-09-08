package com.tddapps.model.aws;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.tddapps.model.Settings;
import com.tddapps.model.SettingsReader;
import lombok.val;

public class DynamoDBMapperFactoryWithTablePrefix implements DynamoDBMapperFactory {
    private final DynamoDBMapper mapper;

    public DynamoDBMapperFactoryWithTablePrefix(SettingsReader settingsReader){
        val tablePrefix = settingsReader.ReadString(Settings.TABLE_PREFIX);

        val client = AmazonDynamoDBClientBuilder.defaultClient();
        val tableNameOverride = DynamoDBMapperConfig.TableNameOverride.withTableNamePrefix(tablePrefix);

        val configBuilder = DynamoDBMapperConfig.builder();
        configBuilder.setTableNameOverride(tableNameOverride);
        DynamoDBMapperConfig config = configBuilder.build();

        mapper = new DynamoDBMapper(client, config);
    }

    @Override
    public DynamoDBMapper getMapper() {
        return mapper;
    }
}
