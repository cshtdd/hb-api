package com.tddapps.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public abstract class DateExtensions {
    public static String ToUtcString(Date value) {
        return ToUtcString(value, "");
    }

    public static String ToUtcString(Date value, String defaultValue){
        if (value == null){
            return defaultValue;
        }

        Instant instant = value.toInstant();
        ZonedDateTime utcDatetime = ZonedDateTime.ofInstant(instant, ZoneId.of("UTC"));
        return utcDatetime.format(DateTimeFormatter.ISO_DATE_TIME);
    }
}
