package com.tddapps.model.aws;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.tddapps.model.Settings;
import com.tddapps.model.SettingsReader;
import lombok.val;

public class DynamoDBMapperFactoryWithTablePrefix implements DynamoDBMapperFactory {
    private final DynamoDBMapper mapper;
    private final SettingsReader settingsReader;

    public DynamoDBMapperFactoryWithTablePrefix(SettingsReader settingsReader, AmazonDynamoDB dynamoDBClient){
        this.settingsReader = settingsReader;

        mapper = new DynamoDBMapper(
                dynamoDBClient,
                getConfigBuilder().build()
        );
    }

    private DynamoDBMapperConfig.Builder getConfigBuilder() {
        val tablePrefix = settingsReader.ReadString(Settings.TABLE_PREFIX);
        val tableNameOverride = DynamoDBMapperConfig.TableNameOverride.withTableNamePrefix(tablePrefix);

        val configBuilder = DynamoDBMapperConfig.builder();
        configBuilder.setTableNameOverride(tableNameOverride);
        return configBuilder;
    }

    @Override
    public DynamoDBMapper getMapper() {
        return mapper;
    }
}
