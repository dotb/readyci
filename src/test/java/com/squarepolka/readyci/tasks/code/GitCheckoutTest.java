package com.squarepolka.readyci.tasks.code;

import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.tasks.readyci.TaskCommandHandler;
import com.squarepolka.readyci.util.PropertyMissingException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static com.squarepolka.readyci.tasks.code.GitCheckout.BUILD_PROP_GIT_BRANCH;
import static com.squarepolka.readyci.tasks.code.GitCheckout.BUILD_PROP_GIT_PATH;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class GitCheckoutTest {

    private static final String COMMAND_GIT = "/usr/bin/git";
    private static final String PARAM_CLONE = "clone";
    private static final String PARAM_BRANCH = "branch";
    private static final String PARAM_SINGLE_BRANCH = "--single-branch";
    private static final String PATH_CODE = "/code/";
    private static final String PATH_GIT = "git://path";
    private static final String PATH_TMP = "/tmp/";
    private static final String PATH_RUN_PATH = "/runpath/";
    private static final String BRANCH_GIT = "git_branch";
    private static final String TEST_BRANCH_VALUE = "feature/sonar_branch_awareness";
    private static final String TEST_BRANCH_OUTPUT = "develop\n" +
            "* feature/sonar_branch_awareness\n" +
            "fix/cleanup\n" +
            "help\n" +
            "master";
    @InjectMocks
    private GitCheckout subject;
    @Mock
    private BuildEnvironment buildEnvironment;
    @Mock
    private TaskCommandHandler taskCommandHandler;

    @Test
    public void checkTaskIdentifier() {
        String taskName = subject.taskIdentifier();
        assertEquals("The GitCheckout task identifier is correct", "checkout_git", taskName);
    }

    @Test
    public void testGitCheckoutWithConfiguredGitPathAndBranchName() {
        String[] expectedCommand = {COMMAND_GIT, PARAM_CLONE, "-b", BRANCH_GIT, PATH_GIT, PATH_CODE};
        String expectedWorkingDir = PATH_TMP;
        Mockito.when(buildEnvironment.getProperty(BUILD_PROP_GIT_PATH)).thenReturn(PATH_GIT);
        Mockito.when(buildEnvironment.getProperty(BUILD_PROP_GIT_BRANCH)).thenReturn(BRANCH_GIT);
        buildEnvironment.codePath = PATH_CODE;
        subject.performTask(buildEnvironment);
        Mockito.verify(taskCommandHandler, Mockito.times(1)).executeCommand(expectedCommand, expectedWorkingDir);
        Mockito.verify(buildEnvironment, Mockito.times(1)).addProperty(BUILD_PROP_GIT_BRANCH, BRANCH_GIT);
    }

    @Test
    public void testGitCheckoutWithConfiguredGitPathAndNoBranchName() {
        String[] expectedCommand = {COMMAND_GIT, PARAM_CLONE, PARAM_SINGLE_BRANCH, PATH_GIT, PATH_CODE};
        String expectedWorkingDir = PATH_TMP;
        Mockito.when(buildEnvironment.getProperty(BUILD_PROP_GIT_PATH)).thenReturn(PATH_GIT);
        Mockito.when(buildEnvironment.getProperty(BUILD_PROP_GIT_BRANCH)).thenThrow(new PropertyMissingException(BUILD_PROP_GIT_BRANCH));
        setupFakeGitCheckout(PATH_CODE);
        subject.performTask(buildEnvironment);
        Mockito.verify(taskCommandHandler, Mockito.times(1)).executeCommand(expectedCommand, expectedWorkingDir);
        Mockito.verify(buildEnvironment, Mockito.times(1)).addProperty(BUILD_PROP_GIT_BRANCH, TEST_BRANCH_VALUE);
    }

    @Test
    public void testGitCheckoutWithNoGitPathAndNoBranchName() {
        String[] expectedCommand = {COMMAND_GIT, PARAM_BRANCH};
        String expectedWorkingDir = PATH_RUN_PATH;
        Mockito.when(buildEnvironment.getProperty(BUILD_PROP_GIT_PATH)).thenThrow(new PropertyMissingException(BUILD_PROP_GIT_BRANCH));
        Mockito.when(buildEnvironment.getProperty(BUILD_PROP_GIT_BRANCH)).thenThrow(new PropertyMissingException(BUILD_PROP_GIT_BRANCH));
        setupFakeGitCheckout(expectedWorkingDir);
        subject.performTask(buildEnvironment);
        Mockito.verify(taskCommandHandler, Mockito.times(1)).executeCommand(expectedCommand, expectedWorkingDir);
        Mockito.verify(buildEnvironment, Mockito.times(1)).addProperty(BUILD_PROP_GIT_BRANCH, TEST_BRANCH_VALUE);
    }

    private void setupFakeGitCheckout(String runPath) {
        String gitBranchCommandOutput = TEST_BRANCH_OUTPUT;
        InputStream fakeInputStream = new ByteArrayInputStream(gitBranchCommandOutput.getBytes(StandardCharsets.UTF_8));
        Mockito.when(taskCommandHandler.executeCommand(new String[]{COMMAND_GIT, PARAM_BRANCH}, runPath)).thenReturn(fakeInputStream);
        buildEnvironment.codePath = PATH_CODE;
        buildEnvironment.realCIRunPath = PATH_RUN_PATH;
    }

}