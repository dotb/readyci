package com.squarepolka.readyci.configuration;

import com.squarepolka.readyci.tasks.code.GitCheckout;

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
    public static final String PIPELINE_PATH_PREFIX_BUILD = "/tmp/readyci/";
    public static final String PIPELINE_PATH_PREFIX_CODE = "/code/";
    public static final String PIPELINE_NAME_DEFAULT = "unknown";

    public String name;
    public Map<String, Object> parameters;
    public List<TaskConfiguration> tasks;

    public PipelineConfiguration() {
        this.name = PIPELINE_NAME_DEFAULT;
        this.parameters = new HashMap<String, Object>();
        this.tasks = new ArrayList<TaskConfiguration>();
    }

    public boolean matchesRepositoryName(String repositoryName, String branch) {
        String gitPath = (String) parameters.get(GitCheckout.BUILD_PROP_GIT_PATH);
        String gitBranch = (String) parameters.get(GitCheckout.BUILD_PROP_GIT_BRANCH);
        return gitPath.toLowerCase().contains(repositoryName.toLowerCase()) &&
                branch.contains(gitBranch) &&
                repositoryName.length() > 0 &&
                branch.length() > 0;
    }

}
