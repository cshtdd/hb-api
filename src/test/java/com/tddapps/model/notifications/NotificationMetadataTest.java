package com.tddapps.model.notifications;

import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class NotificationMetadataTest {
    @Test
    void HasASensibleStringRepresentation(){
        val expected = "NotificationMetadata, subject: this is a test";

        val actual = new NotificationMetadata("this is a test").toString();

        assertEquals(expected, actual);
    }

    @Test
    void CanBeCompared(){
        val m1 = new NotificationMetadata("aaa");
        val m1Copy = new NotificationMetadata("aaa");
        val m2 = new NotificationMetadata("bbb");

        assertEquals(m1, m1Copy);
        assertNotEquals(m1, m2);
    }
}
