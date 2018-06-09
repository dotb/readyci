package com.squarepolka.readyci.tasks;

public interface Task {

    public abstract String taskIdentifier();
    public abstract String description();
    public abstract boolean shouldStopOnFailure();
    public abstract boolean performTask();

}
