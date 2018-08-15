package com.tddapps.dal;

public class HeartBeatRepositoryDynamo implements HeartBeatRepository {
    private final DynamoDBMapperFactory mapperFactory;

    public HeartBeatRepositoryDynamo(DynamoDBMapperFactory mapperFactory){
        this.mapperFactory = mapperFactory;
    }

    @Override
    public void Save(HeartBeat heartBeat) {
        mapperFactory.getMapper().save(heartBeat);
    }
}
