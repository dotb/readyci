package com.squarepolka.readyci.tasks.code;

import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.tasks.Task;
import com.squarepolka.readyci.util.PropertyMissingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.StringTokenizer;

@Component
public class GitCheckout extends Task {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitCheckout.class);
    public static final String TASK_CHECKOUT_GIT = "checkout_git";
    public static final String BUILD_PROP_GIT_PATH = "gitPath";
    public static final String BUILD_PROP_GIT_BRANCH = "gitBranch";
    public static final String CONST_UNKNOWN_GIT_BRANCH = "readyci_unknown_branch";

    public String taskIdentifier() {
        return TASK_CHECKOUT_GIT;
    }

    public void performTask(BuildEnvironment buildEnvironment) {
        try {
            String gitPath = buildEnvironment.getProperty(BUILD_PROP_GIT_PATH);
            LOGGER.debug("The gitPath parameter is specified, so I'll check out the code.");
            try {
                String gitBranch = buildEnvironment.getProperty(BUILD_PROP_GIT_BRANCH);
                buildEnvironment.addProperty(BUILD_PROP_GIT_BRANCH, gitBranch);
                executeCommand(new String[]{"/usr/bin/git", "clone", "-b", gitBranch, gitPath, buildEnvironment.codePath});
            } catch (PropertyMissingException e) {
                LOGGER.debug("gitBranch not specified. Will clone from HEAD");
                executeCommand(new String[]{"/usr/bin/git", "clone", "--single-branch", gitPath, buildEnvironment.codePath});
                String branchName = getCurrentBranchName(buildEnvironment);
                buildEnvironment.addProperty(BUILD_PROP_GIT_BRANCH, branchName);
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
        InputStream inputStream = executeCommand(new String[]{"/usr/bin/git", "branch"}, buildEnvironment.codePath);
        java.util.Scanner scanner = new java.util.Scanner(inputStream).useDelimiter("\\A");
        String allBranches = scanner.hasNext() ? scanner.next() : "";
        return filterBranchName(allBranches);
    }

    protected String filterBranchName(String allBranches) {
        StringTokenizer stringTokenizer = new StringTokenizer(allBranches, "\n");
        while (stringTokenizer.hasMoreElements()) {
            String branchString = stringTokenizer.nextToken();
            if (branchString.contains("*")) {
                return branchString.replace("* ", "");
            }
        }
        return CONST_UNKNOWN_GIT_BRANCH;
    }

}