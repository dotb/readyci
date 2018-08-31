package com.squarepolka.readyci.tasks.app.android;

import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.tasks.Task;
import org.springframework.stereotype.Component;

import java.util.Scanner;
import java.io.Console;

@Component
public class AndroidSignApp extends Task {

    public static final String TASK_SIGN_APP = "android_sign_app";
    public static final String BUILD_PROP_JAVA_KEYSTORE_PATH = "javaKeystorePath";
    public static final String BUILD_PROP_STOREPASS = "storepass";
    public static final String BUILD_PROP_KEYSTORE_ALIAS = "keystoreAlias";
    public static final String BUILD_PROP_SCHEME = "scheme";


    //public static final String BUILD_PROP_TSA_URL = "tsaUrl";


    @Override
    public String taskIdentifier() {
        return TASK_SIGN_APP;
    }

    @Override
    public void performTask(BuildEnvironment buildEnvironment) throws Exception {
        String keystoreAlias = buildEnvironment.getProperty(BUILD_PROP_KEYSTORE_ALIAS);
        String scheme = buildEnvironment.getProperty(BUILD_PROP_SCHEME);
        String keystorePath = buildEnvironment.getProperty(BUILD_PROP_JAVA_KEYSTORE_PATH);
        String storePass = buildEnvironment.getProperty(BUILD_PROP_STOREPASS);
        String apkPath = String.format("%s/app/build/outputs/apk/%s/app-%s-unsigned.apk", buildEnvironment.projectPath, scheme.toLowerCase(), scheme.toLowerCase());

        executeCommand(new String[] {"jarsigner",
                "-verbose",
                "-keystore", keystorePath,
                apkPath,
                keystoreAlias,
                "-storepass", String.valueOf(storePass)
        }, buildEnvironment.realCIRunPath);
    }
}

