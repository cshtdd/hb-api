package com.tddapps.model;

import org.junit.jupiter.api.Test;

import static com.tddapps.utils.EqualityAssertions.*;
import static org.junit.jupiter.api.Assertions.*;


public class NotificationTest {
    @Test
    public void SubjectCannotBeNull(){
        try{
            new Notification(null, "");
            fail("Should have thrown exception");
        }
        catch (NullPointerException e){
            assertNotNull(e);
        }
    }

    @Test
    public void MessageCannotBeNull(){
        try{
            new Notification("", null);
            fail("Should have thrown exception");
        }
        catch (NullPointerException e){
            assertNotNull(e);
        }
    }

    @Test
    public void CanBeCompared(){
        Notification n1 = new Notification("subject 1", "msg1");
        Notification n2 = new Notification("subject 2", "msg2");
        Notification n1Clone = new Notification("subject 1", "msg1");
        Notification n1DifferentSubject = new Notification("subject 2", "msg1");
        Notification n1DifferentMessage = new Notification("subject 1", "msg2");

        shouldBeEqual(n1, n1);
        shouldBeEqual(n1, n1Clone);

        shouldNotBeEqual(n1, null);
        shouldNotBeEqual(n1, "notification");
        shouldNotBeEqual(n1, n2);
        shouldNotBeEqual(n1, n1DifferentSubject);
        shouldNotBeEqual(n1, n1DifferentMessage);
    }

    @Test
    public void HasASensibleStringRepresentation(){
        String actual = new Notification("you got bills", "your total balance is 1000").toString();

        assertEquals("Notification, subject: you got bills, message: your total balance is 1000", actual);
    }
}
