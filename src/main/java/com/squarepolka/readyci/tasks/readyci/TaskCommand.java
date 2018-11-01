package com.squarepolka.readyci.tasks.readyci;

import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.util.PropertyMissingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TaskCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskCommand.class);
    private ArrayList<String> commandAndParams;
    private BuildEnvironment buildEnvironment;

    public TaskCommand(BuildEnvironment buildEnvironment) {
        this.buildEnvironment = buildEnvironment;
        this.commandAndParams = new ArrayList<>();
    }

    public TaskCommand(String command) {
        this.commandAndParams = new ArrayList<>();
        this.commandAndParams.add(command);
    }

    public TaskCommand(String[] command) {
        this.commandAndParams = new ArrayList<>();
        for (int i = 0; i < command.length; i++) {
            String parameter = command[i];
            this.commandAndParams.add(parameter);
        }
    }

    /**
     * Add a string to the list of command parameters
     * @param command - the command to add, for example ls
     * @return TaskCommand - so that you can add more parameter functions
     */
    public TaskCommand addStringCommand(String command) {
        commandAndParams.add(command);
        return this;
    }

    /**
     * Add a string parameter to the list of command line parameters e.g. -Duser=jane
     * @param commandLineKey - command line key. e.g. -Duser
     * @param configurationKey - command line value  e.g. jane
     * @return TaskCommand - so that you can add more parameter functions
     */
    public TaskCommand addStringParameter(String commandLineKey, String configurationKey) {
        try {
            String parameterValue = buildEnvironment.getProperty(configurationKey);
            commandAndParams.add(commandLineKey + "=" + parameterValue);
        } catch (PropertyMissingException e) {
            LOGGER.debug("The String parameter {} is not available. Not adding it to the command", configurationKey);
        }
        return this;
    }

    /**
     * Add a boolean parameter to the list of command line parameters e.g. -Drecursive=true
     * @param commandLineKey - command line key. e.g. -Drecursive
     * @param configurationKey - command line value  e.g. usrRecursiveSearch
     * @return TaskCommand - so that you can add more parameter functions
     */
    public TaskCommand addBooleanParameter(String commandLineKey, String configurationKey) {
        try {
            boolean parameterValue = buildEnvironment.getSwitch(configurationKey);
            commandAndParams.add(commandLineKey + "=" + parameterValue);
        } catch (PropertyMissingException e) {
            LOGGER.debug("The boolean parameter {} is not available. Not adding it to the command", configurationKey);
        }
        return this;
    }

    /**
     * This function will pull a value specified in the build environment and place it into the list of command line parameters
     * if a specified boolean configuration value is true
     * @param commandLineKey - the key that should be placed into the list of command line parameters
     * @param configurationKey - the key for the parameter specified in the yml configuration
     * @param environmentKey - the key used to elicit a value stored in the build environment
     * @return TaskCommand - so that you can add more parameter functions
     */
    public TaskCommand addConditionalEnvironmentValue(String commandLineKey, String configurationKey, String environmentKey) {
        try {
            boolean configuredValue = buildEnvironment.getSwitch(configurationKey);
            if (configuredValue) {
                boolean parameterValue = buildEnvironment.getSwitch(environmentKey);
                commandAndParams.add(commandLineKey + "=" + parameterValue);
            }
        } catch (PropertyMissingException e) {
            LOGGER.debug("The boolean parameter {} is not available. Not adding it to the command", configurationKey);
        }
        return this;
    }

    public List<String> getCommandAndParams() {
        return commandAndParams;
    }

}
