package com.tddapps.model;

import com.tddapps.model.test.HeartBeatFactory;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static com.tddapps.model.test.HeartBeatFactory.TEST_REGION_DEFAULT;
import static com.tddapps.model.test.HeartBeatListTestHelper.ShouldMatch;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RequestHandlerHelperCurrentRegionTest {
    private final SettingsReader settingsReader = mock(SettingsReader.class);
    private final RequestHandlerHelperCurrentRegion helper = new RequestHandlerHelperCurrentRegion(settingsReader);

    @BeforeEach
    public void Setup(){
        when(settingsReader.ReadString(Settings.AWS_REGION)).thenReturn(TEST_REGION_DEFAULT);
    }

    @Test
    public void FilterReturnsAnEmptyListWhenGivenAnEmptyList(){
        assertEquals(0, helper.filter(new HeartBeat[]{}).length);
    }

    @Test
    public void FilterReturnsUniqueNonTestHeartBeatsFromTheCurrentRegion(){
        val seededHeartBeats = HeartBeatFactory.Create(14);
        seededHeartBeats[8].setRegion("us-test-2");
        seededHeartBeats[9].setRegion("us-test-2");
        seededHeartBeats[10].setTest(true);
        seededHeartBeats[11].setTest(true);
        seededHeartBeats[12] = seededHeartBeats[2];
        seededHeartBeats[13] = seededHeartBeats[3];
        val expected = Arrays.copyOfRange(seededHeartBeats, 0, 8);

        val actual = helper.filter(seededHeartBeats);

        ShouldMatch(expected, actual);
    }
}
