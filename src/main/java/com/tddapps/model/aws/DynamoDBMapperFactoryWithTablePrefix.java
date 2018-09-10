package com.tddapps.model.aws;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.tddapps.model.Settings;
import com.tddapps.model.SettingsReader;
import lombok.extern.log4j.Log4j2;
import lombok.val;

@Log4j2
public class DynamoDBMapperFactoryWithTablePrefix implements DynamoDBMapperFactory {
    private final DynamoDBMapper mapper;
    private final SettingsReader settingsReader;

    public DynamoDBMapperFactoryWithTablePrefix(SettingsReader settingsReader){
        this.settingsReader = settingsReader;

        mapper = new DynamoDBMapper(
                getDynamoDbClient(),
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

    private AmazonDynamoDB getDynamoDbClient() {
        val dynamoDbEndpointOverride = settingsReader.ReadString(Settings.DYNAMO_DB_ENDPOINT_OVERRIDE);
        if (dynamoDbEndpointOverride.isEmpty()){
            return getDynamoDBClientDefault();
        }

        return getDynamoDbClientLocal(dynamoDbEndpointOverride);
    }

    private AmazonDynamoDB getDynamoDBClientDefault() {
        return AmazonDynamoDBClientBuilder.defaultClient();
    }

    private AmazonDynamoDB getDynamoDbClientLocal(String dynamoDbEndpointOverride) {
        log.info(String.format("Override Dynamo DB Endpoint; endpoint: %s", dynamoDbEndpointOverride));

        val endpointConfiguration = new AwsClientBuilder.EndpointConfiguration(
                dynamoDbEndpointOverride,
                Regions.DEFAULT_REGION.getName()
        );

        return AmazonDynamoDBClientBuilder
                .standard()
                .withEndpointConfiguration(endpointConfiguration)
                .build();
    }

    @Override
    public DynamoDBMapper getMapper() {
        return mapper;
    }
}
