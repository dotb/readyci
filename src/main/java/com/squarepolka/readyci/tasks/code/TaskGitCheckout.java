package com.squarepolka.readyci.tasks.code;

import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.tasks.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskGitCheckout extends Task {

    public String taskIdentifier() {
        return "checkout_git";
    }

    public String description() {
        return "fetching code from ....";
    }

    public void performTask(BuildEnvironment buildEnvironment) {
        String command = String.format("/usr/bin/git clone %s %s/", buildEnvironment.gitPath, buildEnvironment.buildPath);
        executeCommand(command);
    }
}
