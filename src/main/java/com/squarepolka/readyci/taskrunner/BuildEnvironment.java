package com.squarepolka.readyci.taskrunner;

import com.squarepolka.readyci.configuration.PipelineConfiguration;
import com.squarepolka.readyci.util.PropertyMissingException;

import java.util.*;

public class BuildEnvironment {

    public String pipelineName;
    public String buildUUID;
    public String buildPath;
    public String gitPath;
    public String gitBranch;
    public String projectPath;
    public Map<String, List<String>> buildParameters;

    public BuildEnvironment(PipelineConfiguration configuration) {
        this.pipelineName = configuration.name;
        this.buildUUID = UUID.randomUUID().toString();
        this.gitPath = configuration.gitPath;
        this.gitBranch = configuration.gitBranch;
        this.buildPath = String.format("%s/%s", PipelineConfiguration.PIPELINE_BUILD_PREFIX, buildUUID);
        this.buildParameters = new HashMap<String, List<String>>();
        setBuildParameters(configuration);
        setProjectPath(configuration);
    }

    /**
     * This method copys the pipeline build parameters loaded from the yml file into the
     * buildParameters object. It needs to do type checking and store strings within List<String>
     * so that we can support both String and List<String> values.
     *
     * @param configuration
     */
    private void setBuildParameters(PipelineConfiguration configuration) {
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

    private void setProjectPath(PipelineConfiguration configuration) {
        // Handle the optional project path parameter
        if (configuration.parameters.containsKey(PipelineConfiguration.PIPELINE_PROJECT_PATH)) {
            String projectPath = (String) configuration.parameters.get(PipelineConfiguration.PIPELINE_PROJECT_PATH);
            this.projectPath = String.format("/%s/%s", buildPath, projectPath);
        } else {
            this.projectPath = buildPath;
        }
    }

    public String getProperty(String propertyName, String defaultValue) {
        try {
            String value = getProperty(propertyName);
            return value;
        } catch (PropertyMissingException e) {
            return defaultValue;
        }
    }

    public String getProperty(String propertyName) {
        List<String> values = buildParameters.get(propertyName);
        String value = values.get(0);
        if (null == value) {
            throw new PropertyMissingException(propertyName);
        }
        return value;
    }

    public void addProperty(String propertyName, String propertyValue) {
        List<String> values = buildParameters.get(propertyName);
        if (null == values) {
            values = new ArrayList<String>();
        }
        values.add(propertyValue);
        buildParameters.put(propertyName, values);
    }

    public void addProperty(String propertyName, List<String> propertyValues) {
        List<String> values = buildParameters.get(propertyName);
        if (null == values) {
            values = new ArrayList<String>();
        }
        values.addAll(propertyValues);
        buildParameters.put(propertyName, values);
    }
}
