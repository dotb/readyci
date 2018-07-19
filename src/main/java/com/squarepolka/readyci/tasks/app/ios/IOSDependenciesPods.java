package com.squarepolka.readyci.tasks.app.ios;

import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.tasks.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class IOSDependenciesPods extends Task {

    private static final Logger LOGGER = LoggerFactory.getLogger(IOSDependenciesPods.class);
    public static final String TASK_IOS_POD_INSTALL = "ios_pod_install";

    @Override
    public String taskIdentifier() {
        return TASK_IOS_POD_INSTALL;
    }

    @Override
    public void performTask(BuildEnvironment buildEnvironment) {
        executeCommand(new String[]{"pod", "repo", "update", buildEnvironment.projectPath}
        executeCommand(new String[]{"pod", "install", buildEnvironment.projectPath}
    }
}
