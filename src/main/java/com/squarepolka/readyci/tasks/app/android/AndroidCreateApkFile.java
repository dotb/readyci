package com.squarepolka.readyci.tasks.app.android;

import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.tasks.Task;

public class AndroidCreateApkFile extends Task {

    public static final String TASK_CREATE_APK_FILE = "android_create_apk_file";
    public static final String BUILD_PROP_SCHEME = "scheme";


    @Override
    public String taskIdentifier() {
        return TASK_CREATE_APK_FILE;
    }

    @Override
    public void performTask(BuildEnvironment buildEnvironment) throws Exception {
        String scheme = buildEnvironment.getProperty(BUILD_PROP_SCHEME);

        String buildVariationCommand = String.format("./gradlew assemble%s", scheme);

        executeCommand(new String[]{buildVariationCommand}, buildEnvironment.projectPath);

    }
}
