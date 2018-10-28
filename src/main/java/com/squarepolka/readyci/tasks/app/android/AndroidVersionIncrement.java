package com.squarepolka.readyci.tasks.app.android;

import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.tasks.Task;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

@Component
public class AndroidVersionIncrement extends Task {


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

        Scanner scanner = null;
        FileWriter fileWriter = null;
        IOException thrownException = null;
        try {
            scanner = new Scanner(versionFile);
            String contents = scanner.useDelimiter("\\Z").next();
            String[] pieces = contents.split("=");
            if (pieces.length == 2) {
                variableName = pieces[0];
                buildNumber = Integer.parseInt(pieces[1]) + 1;
                if (!variableName.isEmpty() && buildNumber > 1) {
                    fileWriter = new FileWriter(versionFile);
                    fileWriter.write(variableName + "=" + buildNumber);
                    fileWriter.flush();
                }
            }
        } catch (IOException e) {
            thrownException = e;
        } finally {
            if (null != scanner) {
                scanner.close();
            }
            if (null != fileWriter) {
                fileWriter.close();
            }
        }
        if (null != thrownException) {
            throw thrownException;
        }
    }
}
