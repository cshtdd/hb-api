package com.tddapps.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

import static com.tddapps.utils.DateExtensions.*;
import static org.junit.Assert.assertEquals;

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
        Date expected = Date.from(ZonedDateTime.now(ZoneId.of("UTC")).toInstant());

        assertEquals(expected, UtcNow());
    }
}
