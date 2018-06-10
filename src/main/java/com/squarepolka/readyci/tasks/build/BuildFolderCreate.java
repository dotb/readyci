package com.squarepolka.readyci.tasks.build;

import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.tasks.Task;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class BuildFolderCreate extends Task {
    @Override
    public String taskIdentifier() {
        return "build_path_create";
    }

    @Override
    public void performTask(BuildEnvironment buildEnvironment) {
        String buildPath = buildEnvironment.buildPath;

        StringBuilder pathStrBuilder = new StringBuilder();
        String[] pathFolders = buildPath.split("/");
        for (String folder : pathFolders) {
            pathStrBuilder.append("/");
            pathStrBuilder.append(folder);

            if (!folderExists(pathStrBuilder.toString())) {
                createFolder(pathStrBuilder.toString());
            }
        }
    }

    public boolean folderExists(String path) {
        File folder = new File(path);
        return folder.exists();
    }

    public void createFolder(String path) {
        String command = String.format("/bin/mkdir %s", path);
        executeCommand(command);
    }
}
