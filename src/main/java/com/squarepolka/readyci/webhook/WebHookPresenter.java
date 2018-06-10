package com.squarepolka.readyci.webhook;

import com.squarepolka.readyci.configuration.ReadyCIConfiguration;
import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.taskrunner.TaskRunner;
import com.squarepolka.readyci.taskrunner.TaskRunnerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


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
        ReadyCIConfiguration configuration = ReadyCIConfiguration.instance();
        BuildEnvironment buildEnvironment = new BuildEnvironment(configuration.gitpath);
        TaskRunner taskRunner = taskRunnerFactory.createTaskRunner(buildEnvironment, configuration.tasks);
        taskRunner.runTasks();
    }
}
