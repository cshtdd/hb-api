package com.tddapps.model.aws;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.tddapps.model.DalException;
import com.tddapps.model.HeartBeat;
import com.tddapps.model.HeartBeatRepository;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Arrays;

@Log4j2
public class HeartBeatRepositoryDynamo implements HeartBeatRepository {
    private final DynamoDBMapper mapper;

    public HeartBeatRepositoryDynamo(DynamoDBMapper mapper){
        this.mapper = mapper;
    }

    @Override
    public void Save(HeartBeat heartBeat) throws DalException {
        try {
            mapper.save(heartBeat);
        } catch (AmazonClientException e){
            log.error("HeartBeat Save Error", e);
            throw new DalException(e.getMessage());
        }
    }

    @Override
    public void Save(HeartBeat[] heartBeat) throws DalException {
        try {
            mapper.batchWrite(Arrays.asList(heartBeat), new ArrayList<HeartBeat>());
        } catch (AmazonClientException e){
            log.error("HeartBeat Save Error", e);
            throw new DalException(e.getMessage());
        }
    }

    @Override
    public HeartBeat[] All() throws DalException {
        try {
            return mapper
                    .scan(HeartBeat.class, new DynamoDBScanExpression())
                    .toArray(new HeartBeat[0]);
        } catch (AmazonClientException e){
            log.error("HeartBeat All Error", e);
            throw new DalException(e.getMessage());
        }
    }
}
