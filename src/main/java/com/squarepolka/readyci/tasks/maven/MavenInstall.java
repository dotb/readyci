package com.squarepolka.readyci.tasks.maven;

import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.tasks.Task;
import org.springframework.stereotype.Component;

@Component
public class MavenInstall extends Task {

    public static String TASK_MAVEN_BUILD = "maven_install";

    @Override
    public String taskIdentifier() {
        return TASK_MAVEN_BUILD;
    }

    @Override
    public void performTask(BuildEnvironment buildEnvironment) throws Exception {
        executeCommand(new String[] {"mvn", "install"}, buildEnvironment.projectPath);
    }
}
