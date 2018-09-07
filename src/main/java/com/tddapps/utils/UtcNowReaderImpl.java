package com.tddapps.utils;

import java.util.Date;

import static com.tddapps.utils.DateExtensions.UtcNow;

public class UtcNowReaderImpl implements UtcNowReader {
    @Override
    public Date Read() {
        return UtcNow();
    }
}
