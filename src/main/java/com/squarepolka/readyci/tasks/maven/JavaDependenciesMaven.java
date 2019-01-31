package com.squarepolka.readyci.tasks.maven;

import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.taskrunner.TaskFailedException;
import com.squarepolka.readyci.tasks.Task;
import org.springframework.stereotype.Component;

@Component
public class JavaDependenciesMaven extends Task {

    public static final String TASK_MAVEN_BUILD = "maven_install";

    @Override
    public String taskIdentifier() {
        return TASK_MAVEN_BUILD;
    }

    @Override
    public void performTask(BuildEnvironment buildEnvironment) throws TaskFailedException {
        executeCommand(new String[] {"mvn", "install"}, buildEnvironment.getProjectPath());
    }
}
