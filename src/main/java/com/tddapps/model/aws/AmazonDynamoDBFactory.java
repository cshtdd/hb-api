package com.tddapps.model.aws;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.tddapps.model.Settings;
import com.tddapps.model.SettingsReader;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.picocontainer.PicoContainer;
import org.picocontainer.injectors.FactoryInjector;

import java.lang.reflect.Type;

@Log4j2
public class AmazonDynamoDBFactory extends FactoryInjector<AmazonDynamoDB> {
    @Override
    public AmazonDynamoDB getComponentInstance(PicoContainer container, Type into) {
        val settingsReader = container.getComponent(SettingsReader.class);

        return createClient(settingsReader);
    }

    public AmazonDynamoDB createClient(SettingsReader settingsReader){
        val dynamoDbEndpointOverride = settingsReader.ReadString(Settings.DYNAMO_DB_ENDPOINT_OVERRIDE);
        if (dynamoDbEndpointOverride.isEmpty()){
            return getDefaultClient();
        }

        return getLocalClient(dynamoDbEndpointOverride);
    }

    protected AmazonDynamoDB getDefaultClient() {
        return AmazonDynamoDBClientBuilder.defaultClient();
    }

    protected AmazonDynamoDB getLocalClient(String dynamoDbEndpointOverride) {
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
