package com.squarepolka.readyci.webhook;

import com.squarepolka.readyci.configuration.ReadyCIConfiguration;
import com.squarepolka.readyci.taskrunner.TaskRunner;
import com.squarepolka.readyci.taskrunner.TaskRunnerFactory;
import com.squarepolka.readyci.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
public class WebHookPresenter {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebHookPresenter.class);

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
    public void handleWebHook(Map<String, Object> webHookRequest) {

        String pushType = Util.getMappedValueAtPath(webHookRequest, "push.changes.new.type");
        String branchName = Util.getMappedValueAtPath(webHookRequest, "push.changes.new.name");
        String gitAuthor = Util.getMappedValueAtPath(webHookRequest, "push.changes.new.target.author.raw");

        if (Util.valueExists(pushType) && Util.valueExists(branchName)) {
            LOGGER.info(String.format("Webhook received for type %s and branch %s by user %s", pushType, branchName, gitAuthor));
            handleBuildRequest(branchName);
        } else {
            LOGGER.warn("Webhook ignored a request which didn't contain a branch");
        }
    }

    private void handleBuildRequest(String branch) {
        ReadyCIConfiguration configuration = ReadyCIConfiguration.instance();
        if (configuration.gitBranch.equalsIgnoreCase(branch)) {
            LOGGER.info(String.format("Webhook proceeding with build for branch %s", branch));
            TaskRunner taskRunner = taskRunnerFactory.createTaskRunner(configuration);
            taskRunner.runTasks();
        } else {
            LOGGER.warn(String.format("Webhook ignoring build request for branch %s", branch));
        }
    }

}
