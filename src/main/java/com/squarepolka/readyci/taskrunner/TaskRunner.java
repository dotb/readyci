package com.squarepolka.readyci.taskrunner;

import com.squarepolka.readyci.tasks.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

public class TaskRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskRunner.class);

    protected List<Task> tasks;
    protected BuildEnvironment buildEnvironment;

    public TaskRunner(BuildEnvironment buildEnvironment) {
        this.tasks = new ArrayList<Task>();
        this.buildEnvironment = buildEnvironment;
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public void runTasks() {
        LOGGER.info(String.format("RUNNING BUILD %s ", buildEnvironment.buildUUID));
        checkThatTasksExist();
        runEachTask();
        LOGGER.info(String.format("FINISHED BUILD %s ", buildEnvironment.buildUUID));
    }

    private void checkThatTasksExist() {
        if (tasks.size() <= 0) {
            throw new RuntimeException("There are no tasks to run. Add some tasks and then try again.");
        }
    }

    private void runEachTask() {
        for (Task task : tasks) {
            try {
                runTask(task);
                handleTaskSuccess(task);
            } catch (RuntimeException e) {
                handleTaskFailure(task, e);
            }
        }
    }

    private void runTask(Task task) {
        LOGGER.info(String.format("STARTING TASK %s | %s", task.taskIdentifier(), task.description));
        task.performTask(buildEnvironment);
    }

    private void handleTaskSuccess(Task task) {
        LOGGER.info(String.format("COMPLETED TASK %s", task.taskIdentifier()));
    }

    private void handleTaskFailure(Task task, Exception e) {
        String errorMessage = String.format("FAILED TASK %s with exception: %s", task.taskIdentifier(), e);
        LOGGER.info(errorMessage);
        if (task.shouldStopOnFailure()) {
            throw new TaskFailedException(errorMessage);
        }
    }

}
