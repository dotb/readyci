package com.squarepolka.readyci.tasks;

import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.taskrunner.TaskRunner;
import com.squarepolka.readyci.tasks.readyci.TaskCommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;

public abstract class Task {

    private static final Logger LOGGER = LoggerFactory.getLogger(Task.class);
    public TaskRunner taskRunner;
    @Autowired
    public TaskCommandHandler taskCommandHandler;

    public InputStream executeCommand(String command) {
        return executeCommand(new String[] {command});
    }

    public InputStream executeCommand(String command, String workingDirectory) {
        return executeCommand(new String[] {command}, workingDirectory);
    }

    public InputStream executeCommand(String[] command) {
        return executeCommand(command, "/tmp/");
    }

    public InputStream executeCommand(String[] command, String workingDirectory) {
        return taskCommandHandler.executeCommand(command, workingDirectory);
    }

    // Methods than can be implemented by subclasses
    public boolean shouldStopOnFailure() {
        return true;
    }

    // Methods that must be implemented by subclasses
    public abstract String taskIdentifier();
    public abstract void performTask(BuildEnvironment buildEnvironment) throws Exception;

}
