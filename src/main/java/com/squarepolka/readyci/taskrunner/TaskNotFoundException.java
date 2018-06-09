package com.squarepolka.readyci.taskrunner;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(String taskIdentifer) {
        super(String.format("The task %s could not be found. Check the configuration and ensure you used the correct identifier.", taskIdentifer));
    }

}
