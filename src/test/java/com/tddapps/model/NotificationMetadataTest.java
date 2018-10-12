package com.tddapps.model;

import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NotificationMetadataTest {
    @Test
    public void HasASensibleStringRepresentation(){
        val expected = "NotificationMetadata, subject: this is a test";

        val actual = new NotificationMetadata("this is a test").toString();

        assertEquals(expected, actual);
    }
}
