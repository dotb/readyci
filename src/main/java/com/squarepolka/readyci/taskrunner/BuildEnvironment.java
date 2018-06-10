package com.squarepolka.readyci.taskrunner;

import java.util.UUID;

public class BuildEnvironment {

    public String buildUUID;
    public String buildPath;
    public String gitPath;

    public BuildEnvironment(String gitPath) {
        this.buildUUID = UUID.randomUUID().toString();
        this.buildPath = String.format("/tmp/readyci/%s", buildUUID);
        this.gitPath = gitPath;
    }

}
