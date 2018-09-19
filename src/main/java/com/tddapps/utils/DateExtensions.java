package com.tddapps.utils;

import lombok.val;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static java.lang.Math.abs;

public abstract class DateExtensions {
    public static String ToUtcString(Date value) {
        return ToUtcString(value, "");
    }

    public static String ToUtcString(Date value, String defaultValue){
        if (value == null){
            return defaultValue;
        }

        val instant = value.toInstant();
        val utcDatetime = ZonedDateTime.ofInstant(instant, ZoneId.of("UTC"));
        return utcDatetime.format(DateTimeFormatter.ISO_DATE_TIME);
    }

    public static String ToDynamoUtcString(Date value){
        if (value == null){
            throw new NullPointerException("value");
        }

        val instant = value.toInstant();
        val utcDatetime = ZonedDateTime.ofInstant(instant, ZoneId.of("UTC"));
        return utcDatetime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    public static Date UtcNow(){
        val now = ZonedDateTime.now(ZoneId.of("UTC"))
                .toInstant();
        return Date.from(now);
    }

    public static Date UtcNowPlusMs(int milliseconds){
        val result = ZonedDateTime.now(ZoneId.of("UTC"))
                .plus(Duration.ofMillis(milliseconds))
                .toInstant();
        return Date.from(result);
    }

    public static boolean AreAlmostEquals(Date date1, Date date2){
        return AreAlmostEquals(date1, date2, 300);
    }

    public static boolean AreAlmostEquals(Date date1, Date date2, int deltaMs){
        if (date1 == null || date2 == null){
            return date1 == null && date2 == null;
        }

        val delta = Duration.between(date1.toInstant(), date2.toInstant());
        return abs(delta.toMillis()) < abs(deltaMs);
    }

    public static long EpochSecondsNow(){
        return Instant.now().getEpochSecond();
    }

    public static long EpochSecondsPlusMs(int milliseconds){
        return Instant.now().getEpochSecond() + (milliseconds / 1000);
    }
}
