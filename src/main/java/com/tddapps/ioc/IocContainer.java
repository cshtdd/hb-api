package com.tddapps.ioc;

import com.tddapps.actions.*;
import com.tddapps.model.*;
import com.tddapps.model.aws.*;
import com.tddapps.infrastructure.*;
import com.tddapps.utils.*;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.PicoContainer;

import static org.picocontainer.Characteristics.CACHE;

public class IocContainer {
    private static final IocContainer sharedInstance = new IocContainer();

    private final PicoContainer resolver;

    public static IocContainer getInstance(){
        return sharedInstance;
    }

    private IocContainer(){
        resolver = RegisterBindings();
    }

    public <T> T Resolve(Class<T> type){
        return resolver.getComponent(type);
    }

    private PicoContainer RegisterBindings() {
        return new DefaultPicoContainer()
                .addComponent(HeartBeatPostAction.class)
                .addComponent(StatusGetAction.class)
                .addComponent(NotificationCalculatorAction.class)
                .addComponent(HeartBeatRepository.class, HeartBeatRepositoryDynamo.class)
                .addComponent(NotificationSender.class, NotificationSenderSns.class)
                .addComponent(SettingsReader.class, EnvironmentSettingsReader.class)
                .addComponent(UtcNowReader.class, UtcNowReaderImpl.class)
                .addComponent(HeartBeatNotificationBuilder.class, SingleNotificationBuilder.class)
                .as(CACHE).addComponent(DynamoDBMapperFactory.class, DynamoDBMapperFactoryWithTablePrefix.class)
                .as(CACHE).addComponent(KeysCache.class, InMemoryKeysCacheWithExpiration.class);
    }
}
