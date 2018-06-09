package com.squarepolka.readyci.webhook;

import com.squarepolka.readyci.configuration.ReadyCIConfiguration;
import com.squarepolka.readyci.configuration.TaskConfiguration;
import com.squarepolka.readyci.taskrunner.TaskRunner;
import com.squarepolka.readyci.taskrunner.TaskRunnerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WebHookPresenter {

    private WebHookView webHookView;
    private TaskRunnerFactory taskRunnerFactory;

    @Autowired
    public WebHookPresenter(TaskRunnerFactory taskRunnerFactory) {
        this.taskRunnerFactory = taskRunnerFactory;
    }

    public void setView(WebHookView webHookView) {
        this.webHookView = webHookView;
    }

    @Async
    public void handleWebHook() {
        List<TaskConfiguration> taskConfigurationList = ReadyCIConfiguration.instance().tasks;
        TaskRunner taskRunner = taskRunnerFactory.createTaskRunner(taskConfigurationList);
        taskRunner.runTasks();
    }
}
