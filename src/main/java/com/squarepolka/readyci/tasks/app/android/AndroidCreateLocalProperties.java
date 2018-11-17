package com.squarepolka.readyci.tasks.app.android;

import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.tasks.Task;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Component
public class AndroidCreateLocalProperties extends Task {

    public static final String TASK_ANDROID_CREATE_LOCAL_PROPERTIES = "android_create_local_properties";
    public static final String BUILD_PROP_SDK_PATH = "androidSdkPath";
    private String fileName = "local.properties";

    @Override
    public String taskIdentifier() {
        return TASK_ANDROID_CREATE_LOCAL_PROPERTIES;
    }

    @Override
    public void performTask(BuildEnvironment buildEnvironment) throws Exception {

        String sdkPath = System.getenv("ANDROID_HOME");
        if(!buildEnvironment.getProperty(BUILD_PROP_SDK_PATH, "").isEmpty()) {
            sdkPath = buildEnvironment.getProperty(BUILD_PROP_SDK_PATH);
        }

        if(sdkPath == null || sdkPath.isEmpty()) {
            throw new IllegalArgumentException("Could not locate the sdk path, please define ANDROID_HOME or the androidSdkPath in your configuration");
        }

        File localPropertyFile = getLocalPropertiesFile(buildEnvironment);
        IOException thrownException = null;
        try (FileWriter localPropertyFileWriter = new FileWriter(localPropertyFile)) {

            String out = String.format("sdk.dir=%s", sdkPath);
            localPropertyFileWriter.write(out);
            localPropertyFileWriter.flush();
        } catch (IOException e) {
            thrownException = e;
        }

        // Throw the exception if one was raised.
        if (null != thrownException) {
            throw thrownException;
        }
    }

    private File getLocalPropertiesFile(BuildEnvironment buildEnvironment) {
        return new File(String.format("%s/%s", buildEnvironment.getProjectPath(), fileName));
    }

}
