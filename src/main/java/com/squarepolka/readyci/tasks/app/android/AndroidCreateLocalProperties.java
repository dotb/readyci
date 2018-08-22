package com.squarepolka.readyci.tasks.app.android;

import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.tasks.Task;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;

@Component
public class AndroidCreateLocalProperties extends Task {

    public static final String TASK_ANDROID_CREATE_LOCAL_PROPERTIES = "android_create_local_properties";
    private String fileName = "local.properties";

    @Override
    public String taskIdentifier() {
        return TASK_ANDROID_CREATE_LOCAL_PROPERTIES;
    }

    @Override
    public void performTask(BuildEnvironment buildEnvironment) throws Exception {

        //create file
        File localPropFile = getLocalPropertiesFile(buildEnvironment);

        // creates a FileWriter Object
        FileWriter writer = new FileWriter(localPropFile);

        // Writes the content to the file
        String out = String.format("sdk.dir=/Users/%s/Library/Android/sdk", buildEnvironment.username);
        writer.write(out);
        writer.flush();
        writer.close();

    }

    private File getLocalPropertiesFile(BuildEnvironment buildEnvironment) {
//        String filePath = String.format("%s/local.properties", buildEnvironment.buildPath);
        String filePath = String.format("%s/local.properties", buildEnvironment.realCIRunPath);
        File localPropertiesFile = new File(filePath);
        return localPropertiesFile;
    }

}
