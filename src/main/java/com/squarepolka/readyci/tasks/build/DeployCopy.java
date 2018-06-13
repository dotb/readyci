package com.squarepolka.readyci.tasks.build;

import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.tasks.Task;
import org.springframework.stereotype.Component;

@Component
public class DeployCopy extends Task {

    public static String TASK_DEPLOY_COPY = "deploy_copy";
    public static String TASK_DEPLOY_SOURCE_PATH = "deploySrcPath";
    public static String TASK_DEPLOY_DESTINATION_PATH = "deployDstPath";

    @Override
    public String taskIdentifier() {
        return TASK_DEPLOY_COPY;
    }

    @Override
    public void performTask(BuildEnvironment buildEnvironment) throws Exception {
        String sourcePath = buildEnvironment.buildParameters.get(TASK_DEPLOY_SOURCE_PATH);
        String destinationPath = buildEnvironment.buildParameters.get(TASK_DEPLOY_DESTINATION_PATH);

        executeCommand(new String[] {"/bin/cp", sourcePath, destinationPath}, buildEnvironment.projectPath);
    }
}
