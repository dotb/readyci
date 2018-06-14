package com.squarepolka.readyci.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a single pipeline configuration.
 * The parameters Map allows arbitrary values to be stored.
 */
public class PipelineConfiguration {

    public static final String PIPELINE_PROJECT_PATH = "projectPath";
    public static final String PIPELINE_BUILD_PREFIX = "/tmp/readyci/";

    public String name;
    public String gitPath;
    public String gitBranch;
    public Map<String, Object> parameters;
    public List<TaskConfiguration> tasks;

    public PipelineConfiguration() {
        this.name = "unknown";
        this.gitPath = "";
        this.gitBranch = "master";
        this.parameters = new HashMap<String, Object>();
        this.tasks = new ArrayList<TaskConfiguration>();
    }

    public boolean matchesRepositoryName(String repositoryName, String branch) {
        return gitPath.toLowerCase().contains(repositoryName.toLowerCase()) &&
                gitBranch.equalsIgnoreCase(branch) &&
                repositoryName.length() > 0 &&
                branch.length() > 0;
    }

}
