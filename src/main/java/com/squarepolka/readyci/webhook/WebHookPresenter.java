package com.squarepolka.readyci.webhook;

import com.squarepolka.readyci.configuration.PipelineConfiguration;
import com.squarepolka.readyci.configuration.ReadyCIConfiguration;
import com.squarepolka.readyci.taskrunner.TaskRunner;
import com.squarepolka.readyci.taskrunner.TaskRunnerFactory;
import com.squarepolka.readyci.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


@Component
public class WebHookPresenter {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebHookPresenter.class);

    private TaskRunnerFactory taskRunnerFactory;

    @Autowired
    public WebHookPresenter(TaskRunnerFactory taskRunnerFactory) {
        this.taskRunnerFactory = taskRunnerFactory;
    }

    @Async
    public void handleWebHook(Map<String, Object> webHookRequest) {

        // Handle requests from BitBucket and GitHub. Starting with BitBucket
        String repository = Util.getMappedValueAtPath(webHookRequest, "repository.name");
        String branchName = Util.getMappedValueAtPath(webHookRequest, "push.changes.new.name");
        String gitAuthor = Util.getMappedValueAtPath(webHookRequest, "push.changes.new.target.author.raw");

        // Try load GitHub values if they are missing
        if (!Util.valueExists(branchName)) {
            branchName = Util.getMappedValueAtPath(webHookRequest, "repository.default_branch");
        }
        if (!Util.valueExists(gitAuthor)) {
            gitAuthor = Util.getMappedValueAtPath(webHookRequest, "sender.login");
        }

        if (Util.valueExists(repository) && Util.valueExists(branchName) && Util.valueExists(gitAuthor)) {
            LOGGER.info(String.format("Webhook received for repository %s and branch %s by user %s", repository, branchName, gitAuthor));
            handleBuildRequest(repository, branchName);
        } else {
            LOGGER.warn("Webhook ignored a request which didn't contain a branch");
        }
    }

    private void handleBuildRequest(String repository, String branch) {
        ReadyCIConfiguration configuration = ReadyCIConfiguration.instance();
        List<PipelineConfiguration> pipelineConfigurations = configuration.getPipelines(repository, branch);

        if (pipelineConfigurations.size() > 0) {
            LOGGER.info(String.format("Proceeding with build for %s matched pipelines", pipelineConfigurations.size()));
            runPipelines(pipelineConfigurations);
        } else {
            LOGGER.warn(String.format("Ignoring build request for repository %s branch %s. No matching pipelines configured.", repository, branch));
        }
    }

    private void runPipelines(List<PipelineConfiguration> pipelineConfigurations) {
        for (PipelineConfiguration pipelineConfiguration : pipelineConfigurations) {
            TaskRunner taskRunner = taskRunnerFactory.createTaskRunner(pipelineConfiguration);
            taskRunner.runTasks();
        }
    }

}
