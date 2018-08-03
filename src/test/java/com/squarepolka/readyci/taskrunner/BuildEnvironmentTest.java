package com.squarepolka.readyci.taskrunner;

import com.squarepolka.readyci.configuration.PipelineConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class BuildEnvironmentTest {

    public BuildEnvironment subject;
    public PipelineConfiguration pipelineConfiguration;
    Map<String, Object> parameters;

    @Before
    public void setUp() {
        pipelineConfiguration = Mockito.mock(PipelineConfiguration.class);
        parameters = Mockito.mock(HashMap.class);
        pipelineConfiguration.parameters = parameters;
        Mockito.when(pipelineConfiguration.parameters.get(PipelineConfiguration.PIPELINE_PROJECT_PATH)).thenReturn("\"project/path\"");
        subject = Mockito.spy(new BuildEnvironment(pipelineConfiguration));
    }

    @Test
    public void setBuildParametersWithString() {
        Map.Entry<String, Object> entry = new AbstractMap.SimpleEntry<String, Object>("testKey", "testValue");
        Set<Map.Entry<String, Object>> set = new HashSet<Map.Entry<String, Object>>();
        set.add(entry);
        Mockito.when(pipelineConfiguration.parameters.entrySet()).thenReturn(set);

        subject.setBuildParameters(pipelineConfiguration);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(subject).addProperty(keyCaptor.capture(), valueCaptor.capture());
        assertEquals("Correct key property has been added", "testKey", keyCaptor.getValue());
        assertEquals("Correct value property has been added", "testValue", valueCaptor.getValue());
    }

    @Test
    public void setBuildParametersWithList() {
        List<String> list = new ArrayList<String>();
        list.add("firstString");
        list.add("secondString");
        Map.Entry<String, Object> entry = new AbstractMap.SimpleEntry<String, Object>("testKey", list);
        Set<Map.Entry<String, Object>> set = new HashSet<Map.Entry<String, Object>>();
        set.add(entry);
        Mockito.when(pipelineConfiguration.parameters.entrySet()).thenReturn(set);

        subject.setBuildParameters(pipelineConfiguration);

        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<List> valueCaptor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(subject).addProperty(keyCaptor.capture(), valueCaptor.capture());

        List resultList = valueCaptor.getValue();
        String firstValue = (String) resultList.get(0);
        String secondValue = (String) resultList.get(1);
        assertEquals("Correct key property has been added", "testKey", keyCaptor.getValue());
        assertEquals("Correct first list property has been added", "firstString", firstValue);
        assertEquals("Correct second list property has been added", "secondString", secondValue);
    }

}
