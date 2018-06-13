package com.squarepolka.readyci.taskrunner;

import com.squarepolka.readyci.configuration.PipelineConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BuildEnvironment {

    public String pipelineName;
    public String buildUUID;
    public String buildPath;
    public String gitPath;
    public String gitBranch;
    public String projectPath;
    public Map<String, String> buildParameters;

    public BuildEnvironment(PipelineConfiguration configuration) {
        this.pipelineName = configuration.name;
        this.buildUUID = UUID.randomUUID().toString();
        this.gitPath = configuration.gitPath;
        this.gitBranch = configuration.gitBranch;
        this.buildPath = String.format("%s/%s", PipelineConfiguration.PIPELINE_BUILD_PREFIX, buildUUID);
        this.buildParameters = new HashMap<String, String>();
        this.buildParameters.putAll(configuration.parameters);

        // Handle the optional project path parameter
        if (configuration.parameters.containsKey(PipelineConfiguration.PIPELINE_PROJECT_PATH)) {
            String projectPath = configuration.parameters.get(PipelineConfiguration.PIPELINE_PROJECT_PATH);
            this.projectPath = String.format("/%s/%s", buildPath, projectPath);
        } else {
            this.projectPath = buildPath;
        }
    }

}
