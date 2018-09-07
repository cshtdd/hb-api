package com.tddapps.dal;

import org.junit.jupiter.api.Test;

import static com.tddapps.utils.EqualityAssertions.*;
import static org.junit.jupiter.api.Assertions.*;


public class NotificationTest {
    @Test
    public void CanBeCompared(){
        Notification nNoSubject = new Notification(null, "");
        Notification nNoMessage = new Notification("", null);
        Notification n1 = new Notification("subject 1", "msg1");
        Notification n2 = new Notification("subject 2", "msg2");
        Notification n1Clone = new Notification("subject 1", "msg1");
        Notification n1DifferentSubject = new Notification("subject 2", "msg1");
        Notification n1DifferentMessage = new Notification("subject 1", "msg2");

        shouldBeEqual(n1, n1);
        shouldBeEqual(n1, n1Clone);
        shouldBeEqual(nNoSubject, nNoMessage);

        shouldNotBeEqual(n1, null);
        shouldNotBeEqual(n1, "notification");
        shouldNotBeEqual(n1, nNoSubject);
        shouldNotBeEqual(n1, nNoMessage);
        shouldNotBeEqual(n1, n2);
        shouldNotBeEqual(n1, n1DifferentSubject);
        shouldNotBeEqual(n1, n1DifferentMessage);
    }

    @Test
    public void HasASensibleStringRepresentation(){
        String actual = new Notification("you got bills", "your total balance is 1000").toString();

        assertEquals("Notification, subject: you got bills, message: your total balance is 1000", actual);
    }

    @Test
    public void CanBeCloned(){
        Notification n1 = new Notification("aaaa", "bbb");
        Notification n1Clone = (Notification)n1.clone();

        assertFalse(n1 == n1Clone);
        shouldBeEqual(n1, n1Clone);

        assertEquals(n1.getSubject(), n1Clone.getSubject());
        assertEquals(n1.getMessage(), n1Clone.getMessage());


        n1Clone.setSubject("ccccc");
        n1Clone.setMessage("ddddd");

        assertFalse(n1 == n1Clone);
        shouldNotBeEqual(n1, n1Clone);

        assertNotEquals(n1.getSubject(), n1Clone.getSubject());
        assertNotEquals(n1.getMessage(), n1Clone.getMessage());
    }
}
