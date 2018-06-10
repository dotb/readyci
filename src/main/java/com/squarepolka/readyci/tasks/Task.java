package com.squarepolka.readyci.tasks;

import com.squarepolka.readyci.configuration.TaskConfiguration;
import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public abstract class Task {

    private static final Logger LOGGER = LoggerFactory.getLogger(Task.class);

    public String description;
    public HashMap<String, String > parameters;

    public Task() {
        this.description = "";
        this.parameters = new HashMap<String, String>();
    }

    public void configure(TaskConfiguration taskConfiguration) {
        this.description = taskConfiguration.description;
        this.parameters = taskConfiguration.parameters;
    }

    public boolean shouldStopOnFailure() {
        return true;
    }

    protected void executeCommand(String command) {
        LOGGER.debug(String.format("Executing command: %s", command));
        try {
            Process process = Runtime.getRuntime().exec(command);
            printProcessOutput(process);
            checkProcessSuccess(process);
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

    protected void checkProcessSuccess(Process process) throws InterruptedException {
        int exitValue = process.waitFor();
        if (exitValue != 0) {
            throw new TaskExecuteException(String.format("Task %s failed with exit value %d", taskIdentifier(), exitValue));
        }
    }

    // Methods that must be implemented by subclasses
    public abstract String taskIdentifier();
    public abstract void performTask(BuildEnvironment buildEnvironment);

}
