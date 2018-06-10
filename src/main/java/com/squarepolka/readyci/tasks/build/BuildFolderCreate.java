package com.squarepolka.readyci.tasks.build;

import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.tasks.Task;
import org.springframework.stereotype.Component;

@Component
public class BuildFolderCreate extends Task {
    @Override
    public String taskIdentifier() {
        return "build_path_create";
    }

    @Override
    public String description() {
        return "Create a temporary build path to do the rest of our work.";
    }

    @Override
    public boolean shouldStopOnFailure() {
        return true;
    }

    @Override
    public void performTask(BuildEnvironment buildEnvironment) {
        String buildPath = buildEnvironment.buildPath;

        StringBuilder mkdirCommandStringBuilder = new StringBuilder("/bin/mkdir ");
        String[] pathFolders = buildPath.split("/");
        for (String folder : pathFolders) {
            mkdirCommandStringBuilder.append("/");
            mkdirCommandStringBuilder.append(folder);
            executeCommand(mkdirCommandStringBuilder.toString());
        }
    }
}
