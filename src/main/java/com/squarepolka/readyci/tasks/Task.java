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
        return executeCommand(new String[] {command});
    }

    protected InputStream executeCommand(String command, String workingDirectory) {
        return executeCommand(new String[] {command}, workingDirectory);
    }

    protected InputStream executeCommand(String[] command) {
        return executeCommand(command, "/tmp/");
    }

    protected InputStream executeCommand(String[] command, String workingDirectory) {
        LOGGER.debug(String.format("Executing command: %s", arrayToString(command)));
        try {
            File workingDirectoryFile = new File(workingDirectory);
            Process process = Runtime.getRuntime().exec(command, null, workingDirectoryFile);
            InputStream processInputStream = process.getInputStream();
            processInputStream.mark(5120);
            printProcessOutput(process);
            checkProcessSuccess(process);
            resetInputStream(processInputStream);
            return processInputStream;
        } catch (Exception e) {
            TaskExecuteException taskExecuteException = new TaskExecuteException(String.format("Exception while executing task %s: %s. Tried to run %s", taskIdentifier(), e.toString(), arrayToString(command)));
            taskExecuteException.setStackTrace(e.getStackTrace());
            throw taskExecuteException;
        }
    }

    protected void printProcessOutput(Process process) throws IOException {
        BufferedReader processOutputStream = getProcessOutputStream(process.getInputStream());
        BufferedReader processErrorStream = getProcessOutputStream(process.getErrorStream());

        while (process.isAlive()) {
            printStdOut(processOutputStream);
            printStdError(processErrorStream);
        }
    }

    private void printStdError(BufferedReader processErrorStream) throws IOException {
        String processErrorLine = "";
        while (processErrorStream.ready() &&
                (processErrorLine = processErrorStream.readLine()) != null) {
                System.out.println(processErrorLine);
        }
    }

    private void printStdOut(BufferedReader processOutputStream) throws IOException {
        String processOutputLine = "";
        while (processOutputStream.ready() &&
                (processOutputLine = processOutputStream.readLine()) != null
                && LOGGER.isDebugEnabled()) {

                System.out.println(processOutputLine);
        }
    }

    private BufferedReader getProcessOutputStream(InputStream processInputStream) {
        InputStreamReader processStreamReader = new InputStreamReader(processInputStream);
        return new BufferedReader(processStreamReader);
    }

    protected void checkProcessSuccess(Process process) throws InterruptedException {
        int exitValue = process.waitFor();
        if (exitValue != 0) {
            throw new TaskExecuteException(String.format("Exited with %d", exitValue));
        }
    }

    protected String arrayToString(String[] stringArray) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String string : stringArray) {
            stringBuilder.append(string);
            stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }

    protected void resetInputStream(InputStream inputStream) {
        try {
            inputStream.reset();
        } catch(IOException e) {
            LOGGER.warn(String.format("Ignoring an exception while attempting to reset an input stream %s", e.toString()));
        }
    }

    // Methods that must be implemented by subclasses
    public abstract String taskIdentifier();
    public abstract void performTask(BuildEnvironment buildEnvironment) throws Exception;

}
