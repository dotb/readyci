package com.squarepolka.readyci.taskrunner;

import com.squarepolka.readyci.configuration.PipelineConfiguration;
import com.squarepolka.readyci.util.PropertyMissingException;
import com.squarepolka.readyci.util.PropertyTypeException;

import java.util.*;

public class BuildEnvironment {

    public String pipelineName;
    public String buildUUID;
    public String codePath;
    public String projectFolder;
    public String projectPath;
    public String credentialsPath;
    public String scratchPath;
    public String realCIRunPath;
    public String username;
    private Map<String, Object> buildParameters;

    public BuildEnvironment(PipelineConfiguration configuration) {
        this.pipelineName = configuration.name;
        this.buildUUID = UUID.randomUUID().toString();
        this.scratchPath = String.format("%s/%s", PipelineConfiguration.PIPELINE_PATH_PREFIX_BUILD, buildUUID);
        this.codePath = String.format("%s/%s", scratchPath, PipelineConfiguration.PIPELINE_PATH_PREFIX_CODE);
        this.credentialsPath = String.format("%s./build_credentials", this.codePath);
        this.realCIRunPath = System.getProperty("user.dir");
        this.username = System.getProperty("user.name");
        this.buildParameters = new HashMap<String, Object>();

        this.projectFolder = (String) configuration.parameters.get(PipelineConfiguration.PIPELINE_PROJECT_PATH);
        getProjectFolderFromConfiguration(configuration);
        configureProjectPath();
        setBuildParameters(configuration);
    }

    public void addObject(String propertyName, Object propertyValue) {
        List<Object> values = (List<Object>) buildParameters.get(propertyName);
        if (null == values) {
            values = new ArrayList<Object>();
        }
        values.add(propertyValue);
        buildParameters.put(propertyName, values);
    }

    public Object getObject(String propertyName) {
        List<Object> values = getObjects(propertyName);
        Object value = values.get(0);
        return value;
    }

    public List<Object> getObjects(String propertyName) {
        List<Object> values = (List<Object>) buildParameters.get(propertyName);
        if (null == values || values.size() <= 0) {
            throw new PropertyMissingException(propertyName);
        }
        return values;
    }

    /**
     * Fetch a list of environment properties
     * @param propertyName
     * @return list of String property values
     * @throws PropertyMissingException if the property does not exist
     */
    public List<String> getProperties(String propertyName) {
        List<String> values = (List<String>) buildParameters.get(propertyName);
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
        List<String> values = (List<String>) buildParameters.get(propertyName);
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
        List<String> values = (List<String>) buildParameters.get(propertyName);
        if (null == values) {
            values = new ArrayList<>();
        }
        values.addAll(propertyValues);
        buildParameters.put(propertyName, values);
    }

    /**
     * Add a boolean switch value to this build environment
     * @param switchName
     * @param switchValue
     */
    public void addSwitch(String switchName, Boolean switchValue) {
        List<Boolean> values = (List<Boolean>) buildParameters.get(switchName);
        if (null == values) {
            values = new ArrayList<>();
        }
        values.add(switchValue);
        buildParameters.put(switchName, values);
    }

    /**
     * Return a configured boolean value
     * @param switchName
     * @return boolean value of the switch
     */
    public boolean getSwitch(String switchName) {
        List<Boolean> values = (List<Boolean>) buildParameters.get(switchName);
        if (null == values) {
            throw new PropertyMissingException(switchName);
        } else {
            Boolean switchValue = values.get(0);
            if (null == switchValue) {
                throw new PropertyMissingException(switchName);
            }
            return switchValue.booleanValue();
        }
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
            } else if (objectValue instanceof List) {
                List<String> listValue = (List<String>) objectValue;
                addProperty(propertyName, listValue);
            } else if (objectValue instanceof Boolean) {
                Boolean booleanValue = (Boolean) objectValue;
                addSwitch(propertyName, booleanValue);
            } else {
                throw new PropertyTypeException(propertyName);
            }
        }
    }

    public void getProjectFolderFromConfiguration(PipelineConfiguration configuration) {
        if (configuration.parameters.containsKey(PipelineConfiguration.PIPELINE_PROJECT_PATH)) {
            String projectFolder = (String) configuration.parameters.get(PipelineConfiguration.PIPELINE_PROJECT_PATH);
            this.projectFolder = projectFolder;
        } else {
            this.projectFolder = "";
        }
    }

    public void configureProjectPath() {
        this.projectPath = String.format("/%s/%s", codePath, projectFolder);
    }

    @Override
    public String toString() {
        return "BuildEnvironment{" +
                "pipelineName='" + pipelineName + '\'' +
                ", buildUUID='" + buildUUID + '\'' +
                ", codePath='" + codePath + '\'' +
                ", projectFolder='" + projectFolder + '\'' +
                ", projectPath='" + projectPath + '\'' +
                ", scratchPath='" + scratchPath + '\'' +
                ", realCIRunPath='" + realCIRunPath + '\'' +
                ", username='" + username + '\'' +
                ", buildParameters=" + buildParameters +
                '}';
    }
}
