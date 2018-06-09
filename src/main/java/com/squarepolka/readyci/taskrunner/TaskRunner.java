package com.squarepolka.readyci.taskrunner;

import com.squarepolka.readyci.tasks.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TaskRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskRunner.class);

    protected List<Task> tasks;

    public TaskRunner() {
        this.tasks = new ArrayList<Task>();
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public void runTasks() {
        checkThatTasksExist();
        runEachTask();
    }

    private void checkThatTasksExist() {
        if (tasks.size() <= 0) {
            throw new RuntimeException("There are no tasks to run. Add some tasks and then try again.");
        }
    }

    private void runEachTask() {
        for (Task task : tasks) {
            boolean success = runTask(task);
            handleTaskSuccess(task, success);
        }
    }

    private boolean runTask(Task task) {
        LOGGER.info(String.format("STARTING TASK %s | %s", task.taskIdentifier(), task.description()));
        return task.performTask();
    }

    private void handleTaskSuccess(Task task, boolean success) {
        if (success) {
            LOGGER.info(String.format("COMPLETED TASK %s | %s", task.taskIdentifier(), task.description()));
        } else {
            LOGGER.info(String.format("FAILED TASK %s | %s", task.taskIdentifier(), task.description()));
            if (task.shouldStopOnFailure()) {
                throw new TaskFailedException(task);
            }
        }
    }

}
