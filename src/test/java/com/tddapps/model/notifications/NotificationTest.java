package com.tddapps.model.notifications;

import lombok.val;
import org.junit.jupiter.api.Test;

import static com.tddapps.utils.EqualityAssertions.shouldBeEqual;
import static com.tddapps.utils.EqualityAssertions.shouldNotBeEqual;
import static org.junit.jupiter.api.Assertions.*;

class NotificationTest {
    @Test
    void SubjectCannotBeNull(){
        try{
            new Notification(null, "");
            fail("Should have thrown exception");
        }
        catch (NullPointerException e){
            assertNotNull(e);
        }
    }

    @Test
    void MessageCannotBeNull(){
        try{
            new Notification("", null);
            fail("Should have thrown exception");
        }
        catch (NullPointerException e){
            assertNotNull(e);
        }
    }

    @Test
    void CanBeCompared(){
        val n1 = new Notification("subject 1", "msg1");
        val n2 = new Notification("subject 2", "msg2");
        val n1Clone = new Notification("subject 1", "msg1");
        val n1DifferentSubject = new Notification("subject 2", "msg1");
        val n1DifferentMessage = new Notification("subject 1", "msg2");

        shouldBeEqual(n1, n1);
        shouldBeEqual(n1, n1Clone);

        shouldNotBeEqual(n1, null);
        shouldNotBeEqual(n1, "notification");
        shouldNotBeEqual(n1, n2);
        shouldNotBeEqual(n1, n1DifferentSubject);
        shouldNotBeEqual(n1, n1DifferentMessage);
    }

    @Test
    void HasASensibleStringRepresentation(){
        val actual = new Notification("you got bills", "your total balance is 1000").toString();

        assertEquals("Notification, subject: you got bills, message: your total balance is 1000", actual);
    }
}
