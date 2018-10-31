package com.squarepolka.readyci.tasks.quality;

import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.tasks.Task;
import com.squarepolka.readyci.tasks.code.GitCheckout;
import com.squarepolka.readyci.tasks.readyci.TaskCommand;
import org.springframework.stereotype.Component;

@Component
public class SonarqubeRunner extends Task {

    public static final String TASK_SONARQUBE_RUNNER = "sonarqube_runner";
    public static final String BUILD_PROP_SONAR_HOST_URL = "sonarHostUrl";
    public static final String BUILD_PROP_SONAR_LOGIN_KEY = "sonarLoginKey";
    public static final String BUILD_PROP_SONAR_PROJECT_KEY = "sonarProjectKey";
    public static final String BUILD_PROP_SONAR_SOURCE_PATH = "sonarSourcePath";
    public static final String BUILD_PROP_SONAR_BINARY_PATH = "sonarBinaryPath";
    public static final String BUILD_PROP_SONAR_BYPASS_BUILD_WRAPPER = "sonarBypassBuildWrapper";

    @Override
    public String taskIdentifier() {
        return TASK_SONARQUBE_RUNNER;
    }

    @Override
    public void performTask(BuildEnvironment buildEnvironment) {
        TaskCommand taskCommand = new TaskCommand(buildEnvironment);
        taskCommand.addStringCommand("sonar-scanner").
        addStringParameter("-Dsonar.host.url", BUILD_PROP_SONAR_HOST_URL).
        addStringParameter("-Dsonar.login", BUILD_PROP_SONAR_LOGIN_KEY).
        addStringParameter("-Dsonar.projectKey", BUILD_PROP_SONAR_PROJECT_KEY).
        addStringParameter("-Dsonar.sources", BUILD_PROP_SONAR_SOURCE_PATH).
        addStringParameter("-Dsonar.branch.name", GitCheckout.BUILD_PROP_GIT_BRANCH).
        addStringParameter("-Dsonar.java.binaries", BUILD_PROP_SONAR_BINARY_PATH).
        addBooleanParameter("-Dsonar.cfamily.build-wrapper-output.bypass", BUILD_PROP_SONAR_BYPASS_BUILD_WRAPPER);
        executeCommand(taskCommand, buildEnvironment.projectPath);
    }
}
