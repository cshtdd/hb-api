package com.tddapps.ioc;

import com.tddapps.model.*;
import com.tddapps.model.aws.*;
import com.tddapps.infrastructure.*;
import com.tddapps.model.notifications.*;
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
                .addComponent(HeartBeatRepository.class, HeartBeatRepositoryDynamo.class)
                .addComponent(DynamoDBEventParser.class, DynamoDBEventParserMarshaller.class)
                .addComponent(NotificationSender.class, NotificationSenderSns.class)
                .addComponent(SettingsReader.class, EnvironmentSettingsReader.class)
                .addComponent(NowReader.class, NowReaderImpl.class)
                .addComponent(RequestHandlerHelper.class, RequestHandlerHelperCurrentRegion.class)
                .addComponent(HeartBeatNotificationBuilder.class, SingleNotificationBuilder.class)
                .addComponent(HeartBeatChangeEventNotificationBuilder.class, NotificationBuilderGrouped.class)
                .addAdapter(new AmazonDynamoDBFactory())
                .as(CACHE).addAdapter(new DynamoDBMapperFactory())
                .as(CACHE).addComponent(KeysCache.class, InMemoryKeysCacheWithExpiration.class);
    }
}
