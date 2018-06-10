package com.squarepolka.readyci.tasks.build;

import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.tasks.Task;
import org.springframework.stereotype.Component;

@Component
public class BuildFolderClean extends Task {
    @Override
    public String taskIdentifier() {
        return "build_path_clean";
    }

    @Override
    public String description() {
        return "Clean the build folder to ensure the build starts from scratch.";
    }

    @Override
    public boolean shouldStopOnFailure() {
        return true;
    }

    @Override
    public void performTask(BuildEnvironment buildEnvironment) {
        String buildPath = buildEnvironment.buildPath;

        if (buildPath.length() > 12 && buildPath.startsWith("/tmp/readyci")) {
            executeCommand(String.format("rm -fR %s", buildPath));
        }
    }
}
