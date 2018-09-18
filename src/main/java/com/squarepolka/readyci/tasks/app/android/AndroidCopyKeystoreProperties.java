package com.squarepolka.readyci.tasks.app.android;

import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.tasks.Task;
import org.springframework.stereotype.Component;


@Component
public class AndroidCopyKeystoreProperties extends Task {

    public static final String TASK_COPY_KEYSTORE_PROPERTIES = "android_copy_keystore_properties";
    public static final String BUILD_PROP_KEYSTORE_PROPERTIES_PATH = "keystorePropertiesPath";

    @Override
    public String taskIdentifier() {
        return TASK_COPY_KEYSTORE_PROPERTIES;
    }

    @Override
    public void performTask(BuildEnvironment buildEnvironment) throws Exception {
        String keystorePropertiesPath = buildEnvironment.getProperty(BUILD_PROP_KEYSTORE_PROPERTIES_PATH);
        String projectAppFolder = String.format("%s/app", buildEnvironment.projectPath);

        executeCommand(new String[] {
                "cp",
                keystorePropertiesPath,
                buildEnvironment.projectPath
        }, buildEnvironment.projectPath);
    }
}
