package com.squarepolka.readyci.tasks.code;

import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.tasks.Task;
import org.springframework.stereotype.Component;

@Component
public class GitCheckout extends Task {

    public static final String TASK_CHECKOUT_GIT = "checkout_git";
    public static final String BUILD_PROP_GIT_PATH = "gitPath";
    public static final String BUILD_PROP_GIT_BRANCH = "gitBranch";

    public String taskIdentifier() {
        return TASK_CHECKOUT_GIT;
    }

    public String description() {
        return "fetching code from ....";
    }

    public void performTask(BuildEnvironment buildEnvironment) {
        String gitPath = buildEnvironment.getProperty(BUILD_PROP_GIT_PATH);
        executeCommand(new String[] {"/usr/bin/git", "clone",  gitPath, buildEnvironment.buildPath});
    }
}
