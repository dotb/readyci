package com.squarepolka.readyci.taskrunner;

import com.squarepolka.readyci.configuration.PipelineConfiguration;
import com.squarepolka.readyci.util.PropertyMissingException;

import java.util.*;

public class BuildEnvironment {

    public String pipelineName;
    public String buildUUID;
    public String buildPath;
    public String projectPath;
    public String realCIRunPath;
    private Map<String, List<String>> buildParameters;

    public BuildEnvironment(PipelineConfiguration configuration) {
        this.pipelineName = configuration.name;
        this.buildUUID = UUID.randomUUID().toString();
        this.buildPath = String.format("%s/%s", PipelineConfiguration.PIPELINE_BUILD_PREFIX, buildUUID);
        this.realCIRunPath = System.getProperty("user.dir");
        this.buildParameters = new HashMap<String, List<String>>();
        setBuildParameters(configuration);
        updateProjectPaths(configuration);
    }

    /**
     * Fetch a list of environment properties
     * @param propertyName
     * @return list of String property values
     * @throws PropertyMissingException if the property does not exist
     */
    public List<String> getProperties(String propertyName) {
        List<String> values = buildParameters.get(propertyName);
        if (null == values || values.size() <= 0) {
            throw new PropertyMissingException(propertyName);
        }
        return values;
    }

    /**
     * Fetch a single environment property
     * @param propertyName
     * @return a single String property value
     * @throws PropertyMissingException if the property does not exist
     */
    public String getProperty(String propertyName) {
        List<String> values = getProperties(propertyName);
        String value = values.get(0);
        return value;
    }

    /**
     * Fetch a single environment property, specifying a default if the property does not exist
     * @param propertyName
     * @param defaultValue
     * @return The stored property or defaultValue if the property does not exist
     */
    public String getProperty(String propertyName, String defaultValue) {
        try {
            String value = getProperty(propertyName);
            return value;
        } catch (PropertyMissingException e) {
            return defaultValue;
        }
    }

    /**
     * Add an environment property to this build environment
     * @param propertyName
     * @param propertyValue
     */
    public void addProperty(String propertyName, String propertyValue) {
        List<String> values = buildParameters.get(propertyName);
        if (null == values) {
            values = new ArrayList<String>();
        }
        values.add(propertyValue);
        buildParameters.put(propertyName, values);
    }

    /**
     * Add a list of environment properties to this build environment
     * @param propertyName
     * @param propertyValues
     */
    public void addProperty(String propertyName, List<String> propertyValues) {
        List<String> values = buildParameters.get(propertyName);
        if (null == values) {
            values = new ArrayList<String>();
        }
        values.addAll(propertyValues);
        buildParameters.put(propertyName, values);
    }

    /**
     * This method copies the pipeline build parameters loaded from the yml file into the
     * buildParameters object. It needs to do type checking and store strings within List<String>
     * so that we can support both String and List<String> values.
     *
     * @param configuration
     */
    public void setBuildParameters(PipelineConfiguration configuration) {
        for (Map.Entry<String, Object> configParameter : configuration.parameters.entrySet()) {
            String propertyName = configParameter.getKey();
            Object objectValue = configParameter.getValue();
            if (objectValue instanceof String) {
                String stringValue = (String) objectValue;
                addProperty(propertyName, stringValue);
            } else if (objectValue instanceof  List) {
                List<String> stringValue = (List<String>) objectValue;
                addProperty(propertyName, stringValue);
            }
        }
    }

    public void updateProjectPaths(PipelineConfiguration configuration) {
        // Handle the optional project path parameter
        if (configuration.parameters.containsKey(PipelineConfiguration.PIPELINE_PROJECT_PATH)) {
            String projectPath = (String) configuration.parameters.get(PipelineConfiguration.PIPELINE_PROJECT_PATH);
            this.projectPath = String.format("/%s/%s", buildPath, projectPath);
        } else {
            this.projectPath = buildPath;
        }
    }

}
