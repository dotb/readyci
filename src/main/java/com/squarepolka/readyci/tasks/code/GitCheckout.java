package com.squarepolka.readyci.tasks.code;

import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.tasks.Task;
import com.squarepolka.readyci.util.PropertyMissingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GitCheckout extends Task {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitCheckout.class);
    public static final String TASK_CHECKOUT_GIT = "checkout_git";
    public static final String BUILD_PROP_GIT_PATH = "gitPath";
    public static final String BUILD_PROP_GIT_BRANCH = "gitBranch";

    public String taskIdentifier() {
        return TASK_CHECKOUT_GIT;
    }

    public void performTask(BuildEnvironment buildEnvironment) {
        String gitPath = buildEnvironment.realCIRunPath;
        try {
            gitPath = buildEnvironment.getProperty(BUILD_PROP_GIT_PATH);

            String gitBranch = buildEnvironment.getProperty(BUILD_PROP_GIT_BRANCH);

            if (gitBranch == null){
                gitBranch = "master";
            }

            LOGGER.debug("The gitPath parameter is specified, so I'll check out the code.");
            executeCommand(new String[]{"/usr/bin/git", "clone", "-b", gitBranch, gitPath, buildEnvironment.codePath});
        } catch (PropertyMissingException e) {
            LOGGER.debug("The gitPath parameter was not specified, so I'll assume the code is already checked out.");
            buildEnvironment.codePath = buildEnvironment.realCIRunPath;
            buildEnvironment.configureProjectPath();
        }

    }
}
