package com.tddapps.handlers;

import com.tddapps.model.HeartBeatRepository;
import com.tddapps.model.Settings;
import com.tddapps.model.SettingsReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HeartBeatExpiratorTest {
    private final HeartBeatRepository heartBeatRepository = mock(HeartBeatRepository.class);
    private final SettingsReader settingsReader = mock(SettingsReader.class);
    private final HeartBeatExpirator handler = new HeartBeatExpirator(heartBeatRepository, settingsReader);

    @BeforeEach
    public void Setup(){
        when(settingsReader.ReadString(Settings.AWS_REGION)).thenReturn("us-test-1");
    }

    @Test
    public void CanBeConstructedUsingTheDefaultConstructor(){
        assertNotNull(new HeartBeatExpirator());
    }


}
