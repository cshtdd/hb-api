package com.tddapps.model.aws;

import com.tddapps.model.Settings;
import com.tddapps.model.SettingsReader;
import lombok.val;
import lombok.var;
import org.picocontainer.PicoContainer;
import org.picocontainer.injectors.FactoryInjector;

import java.lang.reflect.Type;

public class DynamoDBClientFactoryResolver extends FactoryInjector<DynamoDBClientFactory> {
    @Override
    public DynamoDBClientFactory getComponentInstance(PicoContainer container, Type into) {
        var settingsReader = container.getComponent(SettingsReader.class);
        var clientFactoryDefault = container.getComponent(DynamoDBClientFactoryDefault.class);
        var clientFactoryLocal = container.getComponent(DynamoDBClientFactoryLocal.class);

        return createClient(settingsReader, clientFactoryDefault, clientFactoryLocal);
    }

    public DynamoDBClientFactory createClient(SettingsReader settingsReader,
                                              DynamoDBClientFactory clientFactoryDefault,
                                              DynamoDBClientFactory clientFactoryLocal){
        val dynamoDbEndpointOverride = settingsReader.ReadString(Settings.DYNAMO_DB_ENDPOINT_OVERRIDE);
        if (dynamoDbEndpointOverride.isEmpty()){
            return clientFactoryDefault;
        }

        return clientFactoryLocal;
    }
}
