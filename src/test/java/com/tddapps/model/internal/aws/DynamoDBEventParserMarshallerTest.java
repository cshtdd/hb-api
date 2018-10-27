package com.tddapps.model.internal.aws;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.StreamRecord;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.tddapps.ioc.IocContainer;
import com.tddapps.model.internal.aws.test.EventData;
import lombok.Data;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DynamoDBEventParserMarshallerTest {
    private final DynamoDBEventParserMarshaller parser = new DynamoDBEventParserMarshaller(
            IocContainer.getInstance().Resolve(DynamoDBMapper.class)
    );

    @Data
    private static class SeededEventData {
        final String type;
        final String id;
        final String capacity;
        final String sold;
    }

    private static DynamodbEvent buildInput(SeededEventData... seededEvents){
        List<DynamodbEvent.DynamodbStreamRecord> seededRecords = Arrays.stream(seededEvents)
                .map(e -> {
                    val d = new StreamRecord();
                    d.setKeys(new HashMap<String, AttributeValue>() {{
                        put("id", new AttributeValue(e.getId()));
                    }});

                    val imageData = new HashMap<String, AttributeValue>() {{
                        put("id", new AttributeValue().withS(e.getId()));
                        put("capacity", new AttributeValue().withN(e.getCapacity()));
                        put("sold", new AttributeValue().withN(e.getSold()));
                    }};

                    if (e.getType().equals("INSERT")) {
                        d.setNewImage(imageData);
                    }
                    else {
                        d.setOldImage(imageData);
                    }

                    val result = new DynamodbEvent.DynamodbStreamRecord();
                    result.setEventName(e.getType());
                    result.setDynamodb(d);

                    return result;
                })
                .collect(Collectors.toList());

        val result = new DynamodbEvent();
        result.setRecords(seededRecords);
        return result;
    }

    @Test
    public void ReadsDeletedRecordsReturnsEmptyWhenInputIsEmpty(){
        assertTrue(parser.readDeletions(buildInput(), EventData.class).isEmpty());
    }

    @Test
    public void ReadsInsertedRecordsReturnsEmptyWhenInputIsEmpty(){
        assertTrue(parser.readInsertions(buildInput(), EventData.class).isEmpty());
    }

    @Test
    public void ReadsDeletedRecordsReturnsEmptyWhenNoneFound(){
        val input = buildInput(
                new SeededEventData("INSERT", "1", "10", "0"),
                new SeededEventData("MODIFY", "2", "10", "0")
        );

        assertTrue(parser.readDeletions(input, EventData.class).isEmpty());
    }

    @Test
    public void ReadsInsertedRecordsReturnsEmptyWhenNoneFound(){
        val input = buildInput(
                new SeededEventData("REMOVE", "1", "10", "0"),
                new SeededEventData("MODIFY", "2", "10", "0")
        );

        assertTrue(parser.readInsertions(input, EventData.class).isEmpty());
    }

    @Test
    public void ReadsDeletedRecords(){
        val input = buildInput(
                new SeededEventData("INSERT", "1", "11", "0"),
                new SeededEventData("MODIFY", "2", "12", "0"),
                new SeededEventData("REMOVE", "3", "13", "1"),
                new SeededEventData("REMOVE", "4", "14", "0")
        );

        val records = parser.readDeletions(input, EventData.class);

        assertEquals(2, records.size());
        assertEquals(new EventData("3", 13, true), records.get(0));
        assertEquals(new EventData("4", 14, false), records.get(1));
    }

    @Test
    public void ReadsInsertedRecords(){
        val input = buildInput(
                new SeededEventData("REMOVE", "1", "11", "0"),
                new SeededEventData("MODIFY", "2", "12", "0"),
                new SeededEventData("INSERT", "3", "13", "1"),
                new SeededEventData("INSERT", "4", "14", "0")
        );

        val records = parser.readInsertions(input, EventData.class);

        assertEquals(2, records.size());
        assertEquals(new EventData("3", 13, true), records.get(0));
        assertEquals(new EventData("4", 14, false), records.get(1));
    }
}

