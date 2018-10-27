package com.squarepolka.readyci.tasks.code;

import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.tasks.Task;
import com.squarepolka.readyci.util.PropertyMissingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.InputStream;

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
        try {
            String gitPath = buildEnvironment.getProperty(BUILD_PROP_GIT_PATH);
            LOGGER.debug("The gitPath parameter is specified, so I'll check out the code.");
            try {
                String gitBranch = buildEnvironment.getProperty(BUILD_PROP_GIT_BRANCH);
                executeCommand(new String[]{"/usr/bin/git", "clone", "-b", gitBranch, gitPath, buildEnvironment.codePath});
            } catch (PropertyMissingException e) {
                LOGGER.debug("gitBranch not specified. Will clone from HEAD");
                executeCommand(new String[]{"/usr/bin/git", "clone", "--single-branch", gitPath, buildEnvironment.codePath});
                return;
            }
        } catch (PropertyMissingException e) {
            LOGGER.debug("The gitPath parameter was not specified, so I'll assume the code is already checked out and set the code path to the current directory and configure the build environment accordingly.");
            buildEnvironment.codePath = buildEnvironment.realCIRunPath;
            buildEnvironment.configureProjectPath();
            String branchName = getCurrentBranchName(buildEnvironment);
            buildEnvironment.addProperty(BUILD_PROP_GIT_BRANCH, branchName);
        }

    }

    protected String getCurrentBranchName(BuildEnvironment buildEnvironment) {
        InputStream inputStream = executeCommand(new String[]{"/usr/bin/git", "branch", "|", "grep", "\'*\'"}, buildEnvironment.codePath);
        java.util.Scanner s = new java.util.Scanner(inputStream).useDelimiter("\\A");
        String branchName = s.hasNext() ? s.next() : "";
        return branchName;
    }

}