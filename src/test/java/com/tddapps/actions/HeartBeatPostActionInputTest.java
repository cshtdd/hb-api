package com.tddapps.actions;

import org.junit.jupiter.api.Test;

import static com.tddapps.utils.EqualityAssertions.*;
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
        shouldBeEqual(input1, input1);

        HeartBeatPostActionInput input1Clone = new HeartBeatPostActionInput("foo");
        shouldBeEqual(input1, input1Clone);

        shouldBeEqual(new HeartBeatPostActionInput(null), new HeartBeatPostActionInput(null));
        shouldBeEqual(new HeartBeatPostActionInput(null), new HeartBeatPostActionInput(""));
        shouldBeEqual(new HeartBeatPostActionInput(""), new HeartBeatPostActionInput(""));
        shouldBeEqual(new HeartBeatPostActionInput("foo"), new HeartBeatPostActionInput("foo"));

        shouldNotBeEqual(new HeartBeatPostActionInput(null), new HeartBeatPostActionInput("bar"));
        shouldNotBeEqual(new HeartBeatPostActionInput(""), new HeartBeatPostActionInput("bar"));
        shouldNotBeEqual(new HeartBeatPostActionInput("foo"), new HeartBeatPostActionInput("bar"));
    }
}
