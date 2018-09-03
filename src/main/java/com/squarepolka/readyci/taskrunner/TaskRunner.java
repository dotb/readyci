package com.squarepolka.readyci.taskrunner;

import com.squarepolka.readyci.tasks.Task;
import com.squarepolka.readyci.tasks.TaskExecuteException;
import com.squarepolka.readyci.util.time.TaskTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TaskRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskRunner.class);

    public TaskRunnerFactory taskRunnerFactory;
    protected List<Task> defaultPreTasks;
    protected List<Task> defaultPostTasks;
    protected List<Task> configuredTasks;
    protected BuildEnvironment buildEnvironment;

    public TaskRunner(BuildEnvironment buildEnvironment, TaskRunnerFactory taskRunnerFactory) {
        this.defaultPreTasks = new ArrayList<Task>();
        this.defaultPostTasks = new ArrayList<Task>();
        this.configuredTasks = new ArrayList<Task>();
        this.buildEnvironment = buildEnvironment;
        this.taskRunnerFactory = taskRunnerFactory;
    }

    public void setConfiguredTasks(List<Task> tasks) {
        this.configuredTasks = tasks;
    }

    public void addConfiguredTask(Task task) {
        configuredTasks.add(task);
    }

    public void addDefaultPreTask(Task task) {
        defaultPreTasks.add(task);
    }

    public void addDefaultPostTask(Task task) {
        defaultPostTasks.add(task);
    }

    public void runAllTasks() {
        try {
            LOGGER.info(String.format("EXECUTING\tBUILD\t%s\t(%s)", buildEnvironment.pipelineName, buildEnvironment.buildUUID));
            runTaskList(defaultPreTasks);
            checkThatTasksExist();
            runTaskList(configuredTasks);
            runTaskList(defaultPostTasks);
            LOGGER.info(String.format("COMPLETED\tBUILD\t%s\t(%s)", buildEnvironment.pipelineName, buildEnvironment.buildUUID));
        } catch (RuntimeException e) {
            LOGGER.info(String.format("FAILED\tBUILD\t%s\t(%s)", buildEnvironment.pipelineName, buildEnvironment.buildUUID));
            throw e;
        }
    }

    private void checkThatTasksExist() {
        if (configuredTasks.size() <= 0) {
            throw new RuntimeException("There are no tasks to run. Add some tasks and then try again.");
        }
    }

    private void runTaskList(List<Task> tasks) {
        for (Task task : tasks) {
            try {
                task.taskRunner = this;
                runTask(task);
                task.taskRunner = null;
            } catch (Exception e) {
                handleTaskFailure(task, e);
            }
        }
    }

    private void runTask(Task task) throws Exception {
        LOGGER.info(String.format("RUNNING\tTASK\t%s", task.taskIdentifier()));
        TaskTimer taskTimer = TaskTimer.newStartedTimer();
        task.performTask(buildEnvironment);
        String formattedTime = taskTimer.stopAndGetElapsedTime();
        LOGGER.info(String.format("\t\t\tFINISHED IN %s", formattedTime));
    }

    private void handleTaskFailure(Task task, Exception e) {
        String errorMessage = String.format("FAILED\tTASK\t%s with exception: %s", task.taskIdentifier(), e.toString());
        LOGGER.error(errorMessage);
        if (task.shouldStopOnFailure()) {
            TaskExecuteException taskExecuteException = new TaskExecuteException(errorMessage);
            taskExecuteException.setStackTrace(e.getStackTrace());
            throw taskExecuteException;
        }
    }

}