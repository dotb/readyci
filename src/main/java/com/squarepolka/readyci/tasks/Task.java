package com.squarepolka.readyci.tasks;

import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.taskrunner.TaskRunner;
import com.squarepolka.readyci.tasks.readyci.TaskCommandHandler;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;

public abstract class Task {

    protected TaskRunner taskRunner;
    @Autowired
    private TaskCommandHandler taskCommandHandler;

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
        return taskCommandHandler.executeCommand(command, workingDirectory);
    }

    // Methods than can be implemented by subclasses
    public boolean shouldStopOnFailure() {
        return true;
    }

    // Methods that must be implemented by subclasses
    public abstract String taskIdentifier();
    public abstract void performTask(BuildEnvironment buildEnvironment) throws Exception;


    // Getters and Setters
    public TaskRunner getTaskRunner() {
        return taskRunner;
    }

    public void setTaskRunner(TaskRunner taskRunner) {
        this.taskRunner = taskRunner;
    }
}
