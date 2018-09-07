package com.tddapps.actions;

import com.tddapps.model.HeartBeat;
import org.junit.jupiter.api.Test;

import static com.tddapps.utils.DateExtensions.UtcNowPlusMs;
import static com.tddapps.utils.EqualityAssertions.shouldBeEqual;
import static com.tddapps.utils.EqualityAssertions.shouldNotBeEqual;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class HeartBeatPostActionInputTest {
    @Test
    public void HasASensibleToString(){
        assertEquals("HeartBeatPostActionInput, intervalMs: 5000, hostId: ", new HeartBeatPostActionInput(null, 5000).toString());
        assertEquals("HeartBeatPostActionInput, intervalMs: 2000, hostId: ", new HeartBeatPostActionInput("", 2000).toString());
        assertEquals("HeartBeatPostActionInput, intervalMs: 60000, hostId: my host", new HeartBeatPostActionInput("my host", 60000).toString());
    }

    @Test
    public void CanBeCompared(){
        HeartBeatPostActionInput input1 = new HeartBeatPostActionInput("foo", 5000);
        shouldBeEqual(input1, input1);

        HeartBeatPostActionInput input1Clone = new HeartBeatPostActionInput("foo", 5000);
        shouldBeEqual(input1, input1Clone);

        shouldBeEqual(new HeartBeatPostActionInput(null, 1000), new HeartBeatPostActionInput(null, 1000));
        shouldBeEqual(new HeartBeatPostActionInput(null, 1000), new HeartBeatPostActionInput("", 1000));
        shouldBeEqual(new HeartBeatPostActionInput("", 1000), new HeartBeatPostActionInput("", 1000));
        shouldBeEqual(new HeartBeatPostActionInput("foo", 1000), new HeartBeatPostActionInput("foo", 1000));

        shouldNotBeEqual(new HeartBeatPostActionInput(null, 1000), new HeartBeatPostActionInput("bar", 1000));
        shouldNotBeEqual(new HeartBeatPostActionInput("", 1000), new HeartBeatPostActionInput("bar", 1000));
        shouldNotBeEqual(new HeartBeatPostActionInput("foo", 1000), new HeartBeatPostActionInput("bar", 1000));
        shouldNotBeEqual(new HeartBeatPostActionInput("foo", 3000), new HeartBeatPostActionInput("foo", 1000));
    }

    @Test
    public void CanBeConvertedToAHeartBeat(){
        HeartBeat expected = new HeartBeat("foo", UtcNowPlusMs(5000));

        HeartBeat actual = new HeartBeatPostActionInput("foo", 5000).toHeartBeat();

        assertTrue(expected.almostEquals(actual));
    }
}
