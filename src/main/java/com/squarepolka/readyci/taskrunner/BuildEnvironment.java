package com.squarepolka.readyci.taskrunner;

import com.squarepolka.readyci.configuration.ReadyCIConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BuildEnvironment {

    public String buildUUID;
    public String buildPath;
    public String gitPath;
    public String projectPath;
    public Map<String, String> buildParameters;

    public BuildEnvironment(ReadyCIConfiguration configuration) {
        this.buildUUID = UUID.randomUUID().toString();
        this.buildPath = String.format("/tmp/readyci/%s", buildUUID);
        this.projectPath = String.format("/%s/%s", buildPath, configuration.projectPath);
        this.gitPath = configuration.gitPath;
        this.buildParameters = new HashMap<String, String>();
    }

}
