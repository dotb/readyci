package com.squarepolka.readyci.tasks;

import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.taskrunner.TaskRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Task {

    private static final Logger LOGGER = LoggerFactory.getLogger(Task.class);

    public TaskRunner taskRunner;

    // Methods than can be implemented by subclasses
    public boolean shouldStopOnFailure() {
        return true;
    }

    // Methods that must be implemented by subclasses
    public abstract String taskIdentifier();
    public abstract void performTask(BuildEnvironment buildEnvironment) throws Exception;

}
