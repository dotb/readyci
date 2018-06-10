package com.squarepolka.readyci.tasks;

import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public abstract class Task {

    private static final Logger LOGGER = LoggerFactory.getLogger(Task.class);

    public abstract String taskIdentifier();
    public abstract String description();
    public abstract boolean shouldStopOnFailure();
    public abstract void performTask(BuildEnvironment buildEnvironment);

    protected void executeCommand(String command) {
        LOGGER.debug(String.format("Executing command: %s", command));
        try {
            Process process = Runtime.getRuntime().exec(command);
            printProcessOutput(process);
        } catch (Exception e) {
            throw new TaskExecuteException(String.format("Exception while executing task %s. %s", taskIdentifier(), e.getLocalizedMessage()));
        }

    }

    protected void printProcessOutput(Process process) throws IOException {
        InputStream processInputStream = process.getInputStream();
        InputStreamReader processStreamReader = new InputStreamReader(processInputStream);
        BufferedReader processBufferedStream = new BufferedReader(processStreamReader);

        String processOutputLine = "";
        while (process.isAlive() && (processOutputLine = processBufferedStream.readLine()) != null) {
            System.out.println(processOutputLine);
        }
    }

}
