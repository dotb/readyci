package com.squarepolka.readyci.taskrunner;

import com.squarepolka.readyci.configuration.TaskConfiguration;
import com.squarepolka.readyci.tasks.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskRunnerFactory {

    private List<Task> allTasks;

    @Autowired
    public TaskRunnerFactory(List<Task> allTasks) {
        this.allTasks = allTasks;
    }

    public TaskRunner createTaskRunner(List<TaskConfiguration> taskConfigurations) {
        TaskRunner taskRunner = new TaskRunner();
        addDefaultTasks(taskRunner);
        addConfiguredTasks(taskRunner, taskConfigurations);
        return taskRunner;
    }

    private void addConfiguredTasks(TaskRunner taskRunner, List<TaskConfiguration> taskConfigurations) {
        for (TaskConfiguration taskConfiguration : taskConfigurations) {
            Task task = findTaskForIdentifier(taskConfiguration.type);
            taskRunner.addTask(task);
        }
    }

    private void addDefaultTasks(TaskRunner taskRunner) {
        taskRunner.addTask(findTaskForIdentifier("build_path_clean"));
        taskRunner.addTask(findTaskForIdentifier("build_path_create"));
        taskRunner.addTask(findTaskForIdentifier("checkout_git"));
    }

    private Task findTaskForIdentifier(String taskIdentifer) {
        for (Task task : allTasks) {
            if (task.taskIdentifier().equalsIgnoreCase(taskIdentifer)) {
                return task;
            }
        }
        throw new TaskNotFoundException(taskIdentifer);
    }
}
