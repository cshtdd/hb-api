package com.tddapps.model.notifications;

import com.tddapps.model.notifications.NotificationMetadata;
import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class NotificationMetadataTest {
    @Test
    public void HasASensibleStringRepresentation(){
        val expected = "NotificationMetadata, subject: this is a test";

        val actual = new NotificationMetadata("this is a test").toString();

        assertEquals(expected, actual);
    }

    @Test
    public void CanBeCompared(){
        val m1 = new NotificationMetadata("aaa");
        val m1Copy = new NotificationMetadata("aaa");
        val m2 = new NotificationMetadata("bbb");

        assertEquals(m1, m1Copy);
        assertNotEquals(m1, m2);
    }
}
