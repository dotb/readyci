package com.squarepolka.readyci.tasks.app.android;

import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.tasks.Task;
import org.springframework.stereotype.Component;

@Component
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
        String apkPath = String.format("%s/app/build/outputs/apk/%s/app-%s-unsigned.apk", buildEnvironment.realCIRunPath, scheme.toLowerCase(), scheme.toLowerCase());

//        jarsigner -verbose -keystore my-release-key.jks /Users/gooi/flybuys-android/app/build/outputs/apk/release/app-release-unsigned.apk my-alias

        executeCommand(new String[] {"jarsigner",
                "-verbose",
                "-keystore", keystoreName,
                apkPath,
                keystoreAlias
                }, buildEnvironment.realCIRunPath);


    }
}
