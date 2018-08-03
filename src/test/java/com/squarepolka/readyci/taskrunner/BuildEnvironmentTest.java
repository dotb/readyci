package com.squarepolka.readyci.taskrunner;

import com.squarepolka.readyci.configuration.PipelineConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class BuildEnvironmentTest {

    public BuildEnvironment subject;
    public PipelineConfiguration pipelineConfiguration;

    @Before
    public void setUp() {
        pipelineConfiguration = Mockito.mock(PipelineConfiguration.class);
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("testKey", "testValue");
        pipelineConfiguration.parameters = parameters;
        subject = Mockito.spy(new BuildEnvironment(pipelineConfiguration));
    }

    @Test
    public void setBuildParameters() {
        subject.setBuildParameters(pipelineConfiguration);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(subject).addProperty(keyCaptor.capture(), valueCaptor.capture());
        assertEquals("Correct key property has been added", "testKey", keyCaptor.getValue());
        assertEquals("Correct value property has been added", "testValue", valueCaptor.getValue());
    }
}
