package com.tddapps.model.aws;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.tddapps.model.DalException;
import com.tddapps.model.HeartBeat;
import com.tddapps.model.HeartBeatRepository;
import lombok.extern.log4j.Log4j2;
import lombok.val;

import java.util.ArrayList;
import java.util.Arrays;

import static com.tddapps.utils.ArrayBatchExtensions.Split;

@Log4j2
public class HeartBeatRepositoryDynamo implements HeartBeatRepository {
    public static final int DYNAMO_MAX_BATCH_SIZE = 25;
    private final DynamoDBMapper mapper;

    public HeartBeatRepositoryDynamo(DynamoDBMapper mapper){
        this.mapper = mapper;
    }

    @Override
    public void Save(HeartBeat heartBeat) throws DalException {
        try {
            log.debug("Save Single Heartbeat;");
            mapper.save(heartBeat);
        } catch (AmazonClientException e){
            log.error("HeartBeat Save Error", e);
            throw new DalException(e.getMessage());
        }
    }

    @Override
    public void Save(HeartBeat[] heartBeat) throws DalException {
        try {
            val batches = Split(heartBeat, DYNAMO_MAX_BATCH_SIZE);
            for (int i = 0; i < batches.length; i++) {
                log.debug(String.format("Save; batchIndex:%s, batchCount:%s", i, batches.length));
                mapper.batchWrite(Arrays.asList(batches[i]), new ArrayList<HeartBeat>());
            }
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
