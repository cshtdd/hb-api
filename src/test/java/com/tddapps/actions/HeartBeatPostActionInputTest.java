package com.tddapps.actions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HeartBeatPostActionInputTest {
    @Test
    public void HasASensibleToString(){
        assertEquals("HeartBeatPostActionInput, hostId: ", new HeartBeatPostActionInput(null).toString());
        assertEquals("HeartBeatPostActionInput, hostId: ", new HeartBeatPostActionInput("").toString());
        assertEquals("HeartBeatPostActionInput, hostId: my host", new HeartBeatPostActionInput("my host").toString());
    }

    @Test
    public void CanBeCompared(){
        HeartBeatPostActionInput input1 = new HeartBeatPostActionInput("foo");
        shouldEqual(input1, input1);

        HeartBeatPostActionInput input1Clone = new HeartBeatPostActionInput("foo");
        shouldEqual(input1, input1Clone);

        shouldEqual(new HeartBeatPostActionInput(null), new HeartBeatPostActionInput(null));
        shouldEqual(new HeartBeatPostActionInput(null), new HeartBeatPostActionInput(""));
        shouldEqual(new HeartBeatPostActionInput(""), new HeartBeatPostActionInput(""));
        shouldEqual(new HeartBeatPostActionInput("foo"), new HeartBeatPostActionInput("foo"));

        shouldNotBeEqual(new HeartBeatPostActionInput(null), new HeartBeatPostActionInput("bar"));
        shouldNotBeEqual(new HeartBeatPostActionInput(""), new HeartBeatPostActionInput("bar"));
        shouldNotBeEqual(new HeartBeatPostActionInput("foo"), new HeartBeatPostActionInput("bar"));
    }

    private void shouldEqual(HeartBeatPostActionInput i1, HeartBeatPostActionInput i2){
        assertEquals(i1, i2);
        assertEquals(i2, i1);

        if (i1 != null && i2 != null){
            assertEquals(i1.hashCode(), i2.hashCode());
            assertEquals(i2.hashCode(), i1.hashCode());
        }
    }

    private void shouldNotBeEqual(HeartBeatPostActionInput i1, HeartBeatPostActionInput i2){
        assertNotEquals(i1, i2);
        assertNotEquals(i2, i1);

        if (i1 != null && i2 != null){
            assertNotEquals(i1.hashCode(), i2.hashCode());
            assertNotEquals(i2.hashCode(), i1.hashCode());
        }
    }
}
