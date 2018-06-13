package com.squarepolka.readyci.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles the parsing and storage of multiple pipeline configurations
 */
public class ReadyCIConfiguration {


    public static String ARG_SERVER = "server";
    public static String ARG_PIPELINE = "pipeline=";
    private static final Logger LOGGER = LoggerFactory.getLogger(ReadyCIConfiguration.class);
    private static ReadyCIConfiguration instance;

    public boolean isServerMode;
    public List<PipelineConfiguration> pipelines;
    public PipelineConfiguration piplineToRun;

    public static ReadyCIConfiguration instance() {
        if (null == instance) {
            instance = new ReadyCIConfiguration();
        }
        return instance;
    }

    private ReadyCIConfiguration() {
        this.isServerMode = false;
        this.pipelines = new ArrayList<PipelineConfiguration>();
        this.piplineToRun = null;
    }

    public PipelineConfiguration getPipeline(String pipelineName) {
        for (PipelineConfiguration pipeline : pipelines) {
            if (pipeline.name.equalsIgnoreCase(pipelineName)) {
                return pipeline;
            }
        }
        return null;
    }

    public PipelineConfiguration getPipeline(String repositoryName, String branch) {
        for (PipelineConfiguration pipeline : pipelines) {
            if (pipeline.matchesRepositoryName(repositoryName, branch)) {
                return pipeline;
            }
        }
        return null;
    }

    public void handleInputParameters(String[] arguments) {
        for (String argument : arguments) {
            handleInputArgument(argument);
        }
    }

    private void handleInputArgument(String argument) {
        if (argument.contains(".yml")) {
            loadConfiguration(argument);
        } else if (argument.equalsIgnoreCase(ARG_SERVER)) {
            isServerMode = true;
        } else if (argument.startsWith(ARG_PIPELINE)) {
            runCommandLineBuild(argument);
        } else {
            LOGGER.warn(String.format("Ignoring unknown argument %s", argument));
        }
    }

    private void loadConfiguration(String fileName) {
        YAMLFactory yamlFactory = new YAMLFactory();
        ObjectMapper mapper = new ObjectMapper(yamlFactory);
        try {
            File configurationFile = new File(fileName);
            ReadyCIConfiguration newConfiguration = mapper.readValue(configurationFile, ReadyCIConfiguration.class);
            this.isServerMode = newConfiguration.isServerMode;
            this.pipelines = newConfiguration.pipelines;
            LOGGER.info(String.format("Loaded configuration %s with %s pipelines", fileName, pipelines.size()));
        } catch (Exception e) {
            LoadConfigurationException configurationException = new LoadConfigurationException(String.format("Could not load configuration from %s: %s", fileName, e.toString()));
            configurationException.setStackTrace(e.getStackTrace());
            throw configurationException;
        }
    }

    private void runCommandLineBuild(String argument) {
        String pipelineName = parseBuildName(argument);
        PipelineConfiguration pipelineConfiguration = getPipeline(pipelineName);
        this.piplineToRun = pipelineConfiguration;
    }
    private String parseBuildName(String argument) {
        return argument.split("=")[1];
    }

}
