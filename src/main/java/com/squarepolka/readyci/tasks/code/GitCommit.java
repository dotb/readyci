package com.squarepolka.readyci.tasks.code;

import com.squarepolka.readyci.configuration.ReadyCIConfiguration;
import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.tasks.Task;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GitCommit extends Task {

    public static final String TASK_COMMIT_GIT = "commit_git";
    public static final String BUILD_PROP_GIT_COMMIT_MESSAGE = "gitCommitMessage";
    public static final String BUILD_PROP_GIT_COMMIT_FILE_LIST = "gitCommitFileList";

    @Override
    public String taskIdentifier() {
        return TASK_COMMIT_GIT;
    }

    @Override
    public void performTask(BuildEnvironment buildEnvironment) throws Exception {
        String configuredCommitMessage = buildEnvironment.getProperty(BUILD_PROP_GIT_COMMIT_MESSAGE, "");
        List<String> filesToCommit = buildEnvironment.getProperties(BUILD_PROP_GIT_COMMIT_FILE_LIST);
        String projectPath = buildEnvironment.projectPath;
        String instanceName = ReadyCIConfiguration.instance().getInstanceName();
        String commitMessage = String.format("%s: %s", instanceName, configuredCommitMessage);

        stageFiles(filesToCommit, projectPath);
        createCommit(commitMessage, projectPath);
        pushCommit(projectPath);
    }

    private void pushCommit(String projectPath) {
        executeCommand(new String[] {"/usr/bin/git",
                "push"}, projectPath);
    }

    private void createCommit(String commitMessage, String projectPath) {
        executeCommand(new String[] {"/usr/bin/git",
                        "commit",
                        "-m",
                        commitMessage}, projectPath);
    }

    private void stageFiles(List<String> filesToCommit, String projectPath) {
        for (String relativePath : filesToCommit) {
            String fullPath = String.format("%s/%s", projectPath, relativePath);
            executeCommand(new String[]{"/usr/bin/git",
                    "add",
                    fullPath}, projectPath);
        }
    }

}
