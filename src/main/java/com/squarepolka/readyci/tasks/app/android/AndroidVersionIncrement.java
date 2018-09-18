package com.squarepolka.readyci.tasks.app.android;

import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.tasks.Task;
import com.squarepolka.readyci.tasks.code.GitCheckout;
import org.springframework.stereotype.Component;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.FileWriter;
import java.util.Scanner;

@Component
public class AndroidVersionIncrement extends Task {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitCheckout.class);

    static final String TASK_INCREMENT_VERSION = "android_task_increment";
    static final String VERSION_PROP_FILE = "version.properties";

    String variableName;
    int buildNumber;

    @Override
    public String taskIdentifier() {
        return TASK_INCREMENT_VERSION;
    }

    @Override
    public void performTask(BuildEnvironment buildEnvironment) throws Exception {
        File versionFile = new File(String.format("%s/"+VERSION_PROP_FILE, buildEnvironment.projectPath));
        String contents = new Scanner(versionFile).useDelimiter("\\Z").next();
        String[] pieces = contents.split("=");
        if(pieces.length == 2) {
            variableName = pieces[0];
            buildNumber = Integer.parseInt(pieces[1]) + 1;
            if(!variableName.isEmpty() && buildNumber > 1) {
                FileWriter writer = new FileWriter(versionFile);
                writer.write(variableName+"="+buildNumber);
                writer.flush();
                writer.close();
            }
        }
    }
}
