package com.squarepolka.readyci.tasks.readyci;

import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.util.PropertyMissingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

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

    public TaskCommand addStringCommand(String command) {
        commandAndParams.add(command);
        return this;
    }

    public TaskCommand addStringParameter(String commandLineKey, String configurationKey) {
        try {
            String parameterValue = buildEnvironment.getProperty(configurationKey);
            commandAndParams.add(commandLineKey + "=" + parameterValue);
        } catch (PropertyMissingException e) {
            LOGGER.debug("The String parameter {} is not available. Not adding it to the command", configurationKey);
        }
        return this;
    }

    public TaskCommand addBooleanParameter(String commandLineKey, String configurationKey) {
        try {
            boolean parameterValue = buildEnvironment.getSwitch(configurationKey);
            commandAndParams.add(commandLineKey + "=" + parameterValue);
        } catch (PropertyMissingException e) {
            LOGGER.debug("The boolean parameter {} is not available. Not adding it to the command", configurationKey);
        }
        return this;
    }

    public ArrayList<String> getCommandAndParams() {
        return commandAndParams;
    }

}
