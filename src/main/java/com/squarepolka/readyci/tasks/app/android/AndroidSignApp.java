package com.squarepolka.readyci.tasks.app.android;

import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.tasks.Task;

public class AndroidSignApp extends Task {

    public static final String TASK_SIGN_APP = "android_sign_app";
    public static final String BUILD_PROP_KEYSTORE_NAME = "keystoreName";
    public static final String BUILD_PROP_KEYSTORE_ALIAS = "keystoreAlias";
    public static final String BUILD_PROP_SCHEME = "scheme";
    //public static final String BUILD_PROP_TSA_URL = "tsaUrl";


    @Override
    public String taskIdentifier() {
        return TASK_SIGN_APP;
    }

    @Override
    public void performTask(BuildEnvironment buildEnvironment) throws Exception {
        String keystoreName = buildEnvironment.getProperty(BUILD_PROP_KEYSTORE_NAME);
        String keystoreAlias = buildEnvironment.getProperty(BUILD_PROP_KEYSTORE_ALIAS);
        String scheme = buildEnvironment.getProperty(BUILD_PROP_SCHEME);
        String apkPath = String.format("~/app/build/outputs/apk/%s/app-%s-unsigned.apk", scheme, scheme);

        executeCommand(new String[] {
                "jarsigner -verbose",
                "-keystore", keystoreName,
                apkPath,
                keystoreAlias,
                "-tsa", "http://sha256timestamp.ws.symantec.com/sha256/timestamp"
        }, buildEnvironment.projectPath);
    }
}
