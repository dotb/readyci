package com.squarepolka.readyci.tasks.app.android;

import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.tasks.Task;
import org.springframework.stereotype.Component;


@Component
public class AndroidFetchBuildDependencies extends Task {

    public static final String TASK_FETCH_BUILD_DEPENDENCIES = "android_fetch_build_dependency";

    @Override
    public String taskIdentifier() {
        return TASK_FETCH_BUILD_DEPENDENCIES;
    }

    @Override
    public void performTask(BuildEnvironment buildEnvironment) throws Exception {

        String credentialsRepository = buildEnvironment.getProperty("credentialsRepository", "");

        if(credentialsRepository.isEmpty()) {
            throw new Exception("credentialsRepository was not provided. We cannot make a build without these artifacts");
        }

        executeCommand(new String[]{"/usr/bin/git", "archive", "--remote", credentialsRepository, "master", "-o", "credentials.tar.gz", buildEnvironment.codePath});

        // read in the project repo yml file and grab the link for the credentials repository from there



        // check out the credentials repository and copy contents into the project directory

        // read in the readyci.yml file

        // load all the variables into memory

//        String keystorePropertiesPath = buildEnvironment.getProperty(BUILD_PROP_KEYSTORE_PROPERTIES_PATH);
//        String projectAppFolder = String.format("%s/app", buildEnvironment.projectPath);
//
//        executeCommand(new String[] {
//                "cp",
//                keystorePropertiesPath,
//                buildEnvironment.projectPath
//        }, buildEnvironment.projectPath);
    }
}
