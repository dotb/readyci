package com.squarepolka.readyci.taskrunner;

import com.squarepolka.readyci.configuration.PipelineConfiguration;
import com.squarepolka.readyci.util.PropertyMissingException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class BuildEnvironmentTest {

    private BuildEnvironment subject;
    private PipelineConfiguration pipelineConfiguration;
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
    public void addObject() {
        List<Object> initialValues = (List<Object>) subject.buildParameters.get("testKey");
        assertEquals("initialValues start as null", null, initialValues);
        subject.addObject("testKey", "testValue");
        List<Object> values = (List<Object>) subject.buildParameters.get("testKey");
        String firstValue = (String) values.get(0);
        assertEquals("buildParameters contains the test value", "testValue", firstValue);
    }

    @Test(expected = PropertyMissingException.class)
    public void getObjectEmpty() {
        List<String> returnedList = (List<String>) subject.getObject("testKey");
    }

    @Test
    public void getObjectPopulated() {
        subject.addObject("testKey", "testValue");
        String returnedValue = (String) subject.getObject("testKey");
        assertEquals("The returned value is populated", "testValue", returnedValue);
    }

    @Test(expected = PropertyMissingException.class)
    public void getObjectsEmpty() {
        subject.getObjects("testKey");
    }

    @Test
    public void getObjectsPopulated() {
        subject.addObject("testKey", "testValue");
        List<Object> returnedList = (List<Object>) subject.getObjects("testKey");
        String testValue = (String) returnedList.get(0);
        assertEquals("The returned list is populated", testValue, testValue);
    }

    @Test(expected = PropertyMissingException.class)
    public void getPropertiesEmpty() {
        subject.getProperties("testKey");
    }

    @Test
    public void getProperties() {
        subject.addProperty("testKey", "testValue");
        List<String> returnedList = subject.getProperties("testKey");
        String testValue = returnedList.get(0);
        assertEquals("The returned list is populated", testValue, testValue);
    }

    @Test(expected = PropertyMissingException.class)
    public void getPropertyEmpty() {
        subject.getProperty("testKey");
    }

    @Test
    public void getPropertyPopulated() {
        subject.addProperty("testKey", "testValue");
        String returnedValue = (String) subject.getProperty("testKey");
        assertEquals("The returned value is populated", "testValue", returnedValue);
    }

    @Test
    public void getPropertyWithDefaultValueEmpty() {
        String returnedValue = subject.getProperty("testKey", "defaultValue");
        assertEquals("The returned value is set to the default", "defaultValue", returnedValue);
    }

    @Test
    public void getPropertyWithDefaultValuePopulated() {
        subject.addProperty("testKey", "testValue");
        String returnedValue = subject.getProperty("testKey", "defaultValue");
        assertEquals("The returned value is set to the stored value", "testValue", returnedValue);
    }

    @Test
    public void addProperty() {
        subject.addProperty("testKey", "testValue");
        String returnedValue = subject.getProperty("testKey");
        assertEquals("The returned value is set to the stored value", "testValue", returnedValue);
    }

    @Test
    public void addPropertyList() {
        List<String> testData = new ArrayList<String>();
        testData.add("testValue");
        subject.addProperty("testKey", testData);
        List<String> returnedList = subject.getProperties("testKey");
        String testValue = returnedList.get(0);
        assertEquals("The returned list is populated", testValue, testValue);
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

    @Test
    public void getProjectFolderFromConfigurationSpecified() {
        Mockito.when(pipelineConfiguration.parameters.containsKey(PipelineConfiguration.PIPELINE_PROJECT_PATH)).thenReturn(true);
        Mockito.when(pipelineConfiguration.parameters.get(PipelineConfiguration.PIPELINE_PROJECT_PATH)).thenReturn("testPath");
        subject.getProjectFolderFromConfiguration(pipelineConfiguration);
        assertEquals("The projectFolder should be set", "testPath", subject.projectFolder);
    }

    @Test
    public void getProjectFolderFromConfigurationNotSpecified() {
        subject.getProjectFolderFromConfiguration(pipelineConfiguration);
        assertEquals("The projectFolder should be empty", "", subject.projectFolder);
    }


    @Test
    public void configureProjectPath() {
        subject.codePath = "codepath";
        subject.projectFolder = "projectfolder";
        subject.configureProjectPath();
        assertEquals("The projectPath is configured correctly", "/codepath/projectfolder", subject.projectPath);
    }

}
