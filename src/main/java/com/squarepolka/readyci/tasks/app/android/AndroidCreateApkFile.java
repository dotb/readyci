package com.squarepolka.readyci.tasks.app.android;

import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.tasks.Task;
import com.squarepolka.readyci.tasks.TaskExecuteException;
import com.squarepolka.readyci.util.PropertyMissingException;
import org.springframework.stereotype.Component;

@Component
public class AndroidCreateApkFile extends Task {

    public static final String TASK_CREATE_APK_FILE = "android_create_apk_file";
    public static final String BUILD_PROP_SCHEME = "scheme";
    public static final String BUILD_PROP_SIGNING_NEEDED = "signingNeeded";


    @Override
    public String taskIdentifier() {
        return TASK_CREATE_APK_FILE;
    }

    @Override
    public void performTask(BuildEnvironment buildEnvironment) throws Exception {
        String scheme = buildEnvironment.getProperty(BUILD_PROP_SCHEME);
        String arg = String.format("assemble%s", scheme);
        String unsignedApkPath = String.format("%s/app/build/outputs/apk/%s/app-%s-unsigned", buildEnvironment.projectPath, scheme.toLowerCase(), scheme.toLowerCase());
        String newApkPath = String.format("%s/app/build/outputs/apk/%s/app-%s", buildEnvironment.projectPath, scheme.toLowerCase(), scheme.toLowerCase());


        System.out.println("types is....."+buildEnvironment.getProperty(BUILD_PROP_SIGNING_NEEDED).getClass())
        ;
        executeCommand(new String[] {
                "./gradlew",
                arg
        }, buildEnvironment.projectPath);

        String signingNeeded = buildEnvironment.getProperty(BUILD_PROP_SIGNING_NEEDED);

        if (signingNeeded=="'true'"){
            executeCommand(new String[] {
                    "mv",
                    unsignedApkPath,
                    newApkPath
            }, buildEnvironment.projectPath);
        }


    }
}
