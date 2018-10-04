package com.squarepolka.readyci.tasks.quality;

import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.tasks.Task;
import org.springframework.stereotype.Component;

@Component
public class SonarqubeRunner extends Task {

    public static final String TASK_SONARQUBE_RUNNER = "sonarqube_runner";
    public static final String BUILD_PROP_SONAR_HOST_URL = "sonarHostUrl";
    public static final String BUILD_PROP_SONAR_LOGIN_KEY = "sonarLoginKey";
    public static final String BUILD_PROP_SONAR_PROJECT_KEY = "sonarProjectKey";
    public static final String BUILD_PROP_SONAR_SOURCE_PATH = "sonarSourcePath";
    public static final String BUILD_PROP_SONAR_BINARY_PATH = "sonarBinaryPath";

    @Override
    public String taskIdentifier() {
        return TASK_SONARQUBE_RUNNER;
    }

    @Override
    public void performTask(BuildEnvironment buildEnvironment) {

        String sonarHostUrl = buildEnvironment.getProperty(BUILD_PROP_SONAR_HOST_URL);
        String sonarLoginKey = buildEnvironment.getProperty(BUILD_PROP_SONAR_LOGIN_KEY);
        String sonarProjectKey = buildEnvironment.getProperty(BUILD_PROP_SONAR_PROJECT_KEY);
        String sonarSourcePath = buildEnvironment.getProperty(BUILD_PROP_SONAR_SOURCE_PATH);
        String sonarBinaryPath = buildEnvironment.getProperty(BUILD_PROP_SONAR_BINARY_PATH);

        executeCommand(new String[] {"sonar-scanner",
                "-Dsonar.host.url=" + sonarHostUrl,
                "-Dsonar.login=" + sonarLoginKey,
                "-Dsonar.projectKey=" + sonarProjectKey,
                "-Dsonar.sources=" + sonarSourcePath,
                "-Dsonar.java.binaries=" + sonarBinaryPath}, buildEnvironment.projectPath);
    }

}
