package com.squarepolka.readyci.tasks;

import com.squarepolka.readyci.configuration.TaskConfiguration;
import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public abstract class Task {

    private static final Logger LOGGER = LoggerFactory.getLogger(Task.class);

    public String description;

    public Task() {
        this.description = "";
    }

    public void configure(TaskConfiguration taskConfiguration) {
        this.description = taskConfiguration.description;
    }

    public boolean shouldStopOnFailure() {
        return true;
    }

    protected InputStream executeCommand(String command) {
        return executeCommand(command, "/tmp/");
    }

    protected InputStream executeCommand(String command, String workingDirectory) {
        LOGGER.debug(String.format("Executing command: %s", command));
        try {
            File workingDirectoryFile = new File(workingDirectory);
            Process process = Runtime.getRuntime().exec(command, null, workingDirectoryFile);
            InputStream processInputStream = process.getInputStream();
            processInputStream.mark(5120);
            printProcessOutput(process);
            checkProcessSuccess(process);
            processInputStream.reset();
            return processInputStream;
        } catch (Exception e) {
            TaskExecuteException taskExecuteException = new TaskExecuteException(String.format("Exception while executing task %s: %s. Tried to run %s", taskIdentifier(), e.toString(), command));
            taskExecuteException.setStackTrace(e.getStackTrace());
            throw taskExecuteException;
        }
    }

    protected void printProcessOutput(Process process) throws IOException {
        BufferedReader processOutputStream = getProcessOutputStream(process.getInputStream());
        BufferedReader processErrorStream = getProcessOutputStream(process.getErrorStream());

        String processOutputLine = "";
        String processErrorLine = "";
        while (process.isAlive()) {
            while ((processOutputLine = processOutputStream.readLine()) != null) {
                    System.out.println(processOutputLine);
            }
            while ((processErrorLine = processErrorStream.readLine()) != null) {
                    System.out.println(processErrorLine);
            }
        }
    }

    protected BufferedReader getProcessOutputStream(InputStream processInputStream) {
        InputStreamReader processStreamReader = new InputStreamReader(processInputStream);
        return new BufferedReader(processStreamReader);
    }

    protected void checkProcessSuccess(Process process) throws InterruptedException {
        int exitValue = process.waitFor();
        if (exitValue != 0) {
            throw new TaskExecuteException(String.format("Exited with %d", exitValue));
        }
    }

    // Methods that must be implemented by subclasses
    public abstract String taskIdentifier();
    public abstract void performTask(BuildEnvironment buildEnvironment) throws Exception;

}
