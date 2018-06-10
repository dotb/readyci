package com.squarepolka.readyci.taskrunner;

import com.squarepolka.readyci.tasks.Task;

public class TaskFailedException extends RuntimeException {
    public Task failedTask;

    public TaskFailedException(Task failedTask) {
        super(String.format("Task %s failed. %s", failedTask.taskIdentifier(), failedTask.description));
        this.failedTask = failedTask;
    }
}
