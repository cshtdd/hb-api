package com.tddapps.utils;

import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.Date;

import static com.tddapps.utils.DateExtensions.*;
import static org.junit.Assert.*;

public class DateExtensionsTest {
    @Test
    public void ToUtcStringReturnsEmptyStringWhenThereIsNoDate(){
        assertEquals("", ToUtcString(null));
    }

    @Test
    public void ToUtcStringReturnsTheDefaultValueWhenThereIsNoDate() {
        assertEquals("NO_DATE", ToUtcString(null, "NO_DATE"));
    }

    @Test
    public void ToUtcStringReturnsTheIsoStringRepresentationOfAZonedDateTime(){
        ZonedDateTime dateTime = ZonedDateTime.of(2017, 7, 17, 20, 5, 31, 0, ZoneId.of("UTC"));
        Date date = Date.from(dateTime.toInstant());

        assertEquals("2017-07-17T20:05:31Z[UTC]", ToUtcString(date));
    }

    @Test
    public void ToUtcStringReturnsTheIsoStringRepresentationOfALocalDateTime(){
        LocalDateTime dateTime = LocalDateTime.of(2017, 7, 17, 20, 5, 31);
        Date date = Date.from(dateTime.toInstant(ZoneOffset.UTC));

        assertEquals("2017-07-17T20:05:31Z[UTC]", ToUtcString(date));
    }

    @Test
    public void ToUtcStringReturnsTheIsoStringRepresentationWithTheTimezoneOffset(){
        LocalDateTime dateTime = LocalDateTime.of(2017, 7, 17, 20, 5, 31);
        Date date = Date.from(dateTime.toInstant(ZoneOffset.ofHours(3)));

        assertEquals("2017-07-17T17:05:31Z[UTC]", ToUtcString(date));
    }

    @Test
    public void UtcNowReturnsTheCorrectValue(){
        Instant expected = ZonedDateTime.now(ZoneId.of("UTC")).toInstant();
        Instant actual = UtcNow().toInstant();

        long delta = Duration.between(expected, actual).toMillis();

        assertTrue(delta < 100);
    }

    @Test
    public void UtcNowPlusAddsTheCorrectNumberOfMilliseconds(){
        Instant expected = ZonedDateTime.now(ZoneId.of("UTC"))
                .plusNanos(40000)
                .toInstant();
        Instant actual = UtcNowPlusMs(40).toInstant();

        long delta = Duration.between(expected, actual).toMillis();

        assertTrue(delta < 100);
    }

    @Test
    public void UtcNowPlusDoesNotReturnNow(){
        Instant now = ZonedDateTime.now(ZoneId.of("UTC")).toInstant();
        Instant actual = UtcNowPlusMs(400).toInstant();

        long delta = Duration.between(now, actual).toMillis();

        assertTrue(delta > 100);
    }

    @Test
    public void UtcNowPlusSupportsNegativeParameters(){
        Instant expected = ZonedDateTime.now(ZoneId.of("UTC"))
                .plusNanos(-45000)
                .toInstant();
        Instant actual = UtcNowPlusMs(-45).toInstant();

        long delta = Duration.between(expected, actual).toMillis();

        assertTrue(delta < 100);
    }

    @Test
    public void AreAlmostEqualsWorksAsExpected(){
        assertTrue(AreAlmostEquals(UtcNow(), UtcNow()));

        assertFalse(AreAlmostEquals(UtcNow(), UtcNowPlusMs(400)));
        assertFalse(AreAlmostEquals(UtcNowPlusMs(400), UtcNow()));

        assertFalse(AreAlmostEquals(UtcNow(), UtcNowPlusMs(-400)));
        assertFalse(AreAlmostEquals(UtcNowPlusMs(-400), UtcNow()));

        Date date1 = Date.from(ZonedDateTime.now(ZoneId.of("UTC"))
                .plusNanos(80000)
                .toInstant());

        assertTrue(AreAlmostEquals(date1, UtcNowPlusMs(80)));
        assertTrue(AreAlmostEquals(UtcNowPlusMs(80), date1));

        assertTrue(AreAlmostEquals(date1, UtcNowPlusMs(85)));
        assertTrue(AreAlmostEquals(UtcNowPlusMs(85), date1));

        assertFalse(AreAlmostEquals(date1, UtcNowPlusMs(385)));
        assertFalse(AreAlmostEquals(UtcNowPlusMs(385), date1));
    }
}
