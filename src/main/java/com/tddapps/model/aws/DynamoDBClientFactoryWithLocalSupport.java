package com.tddapps.model.aws;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.tddapps.model.Settings;
import com.tddapps.model.SettingsReader;
import lombok.extern.log4j.Log4j2;
import lombok.val;

@Log4j2
public class DynamoDBClientFactoryWithLocalSupport implements DynamoDBClientFactory {
    private final SettingsReader settingsReader;
    private final DynamoDBClientFactoryDefault clientFactoryDefault;

    public DynamoDBClientFactoryWithLocalSupport(SettingsReader settingsReader, DynamoDBClientFactoryDefault clientFactoryDefault) {
        this.settingsReader = settingsReader;
        this.clientFactoryDefault = clientFactoryDefault;
    }

    @Override
    public AmazonDynamoDB getClient() {
        val dynamoDbEndpointOverride = settingsReader.ReadString(Settings.DYNAMO_DB_ENDPOINT_OVERRIDE);
        if (dynamoDbEndpointOverride.isEmpty()){
            return clientFactoryDefault.getClient();
        }

        return getDynamoDbClientLocal(dynamoDbEndpointOverride);
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
}
