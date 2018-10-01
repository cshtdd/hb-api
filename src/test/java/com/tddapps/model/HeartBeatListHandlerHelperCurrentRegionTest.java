package com.tddapps.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.tddapps.model.HeartBeatFactory.TEST_REGION_DEFAULT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HeartBeatListHandlerHelperCurrentRegionTest {
    private final SettingsReader settingsReader = mock(SettingsReader.class);
    private final HeartBeatListHandlerHelperCurrentRegion helper = new HeartBeatListHandlerHelperCurrentRegion(settingsReader);

    @BeforeEach
    public void Setup(){
        when(settingsReader.ReadString(Settings.AWS_REGION)).thenReturn(TEST_REGION_DEFAULT);
    }

    @Test
    public void ReturnsAnEmptyListWhenGivenAnEmptyList(){
        assertEquals(0, helper.filter(new HeartBeat[]{}).length);
    }
}
