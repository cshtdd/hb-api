package com.tddapps.ioc;

import com.tddapps.actions.HeartBeatPostAction;
import com.tddapps.actions.StatusGetAction;
import com.tddapps.dal.DynamoDBMapperFactory;
import com.tddapps.dal.DynamoDBMapperFactoryWithTablePrefix;
import com.tddapps.dal.HeartBeatRepository;
import com.tddapps.dal.HeartBeatRepositoryDynamo;
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
                .addComponent(HeartBeatRepository.class, HeartBeatRepositoryDynamo.class)
                .as(CACHE).addComponent(DynamoDBMapperFactory.class, DynamoDBMapperFactoryWithTablePrefix.class);
    }
}
