package com.squarepolka.readyci.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ReadyCIConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReadyCIConfiguration.class);
    private static ReadyCIConfiguration instance;

    public String name;
    public boolean server;
    public List<TaskConfiguration> tasks;

    public static ReadyCIConfiguration instance() {
        if (null == instance) {
            instance = new ReadyCIConfiguration();
        }
        return instance;
    }

    private ReadyCIConfiguration() {
        this.name = "";
        this.server = false;
        this.tasks = new ArrayList<TaskConfiguration>();
    }

    public void handleInputParameters(String[] arguments) {
        for (String argument : arguments) {
            handleInputArgument(argument);
        }
    }

    private void handleInputArgument(String argument) {
        if (argument.equalsIgnoreCase("server")) {
            server = true;
        } else if (argument.contains(".yml")) {
            loadConfiguration(argument);
        }
    }

    private void loadConfiguration(String fileName) {

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            File configurationFile = new File(fileName);
            ReadyCIConfiguration configuration = mapper.readValue(configurationFile, ReadyCIConfiguration.class);
            instance = configuration;
        } catch (Exception e) {
            throw new LoadConfigurationException(String.format("Could not load configuration from %s: %s", fileName, e.getLocalizedMessage()));
        }

    }

}
