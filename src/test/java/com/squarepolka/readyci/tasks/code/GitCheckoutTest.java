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
        String[] expectedCommand = {"/usr/bin/git", "clone", "-b", "git_branch", "git://path", "/code/"};
        String expectedWorkingDir = "/tmp/";
        Mockito.when(buildEnvironment.getProperty(BUILD_PROP_GIT_PATH)).thenReturn("git://path");
        Mockito.when(buildEnvironment.getProperty(BUILD_PROP_GIT_BRANCH)).thenReturn("git_branch");
        buildEnvironment.codePath = "/code/";
        subject.performTask(buildEnvironment);
        Mockito.verify(taskCommandHandler, Mockito.times(1)).executeCommand(expectedCommand, expectedWorkingDir);
        Mockito.verify(buildEnvironment, Mockito.times(1)).addProperty(BUILD_PROP_GIT_BRANCH, "git_branch");
    }

    @Test
    public void testGitCheckoutWithConfiguredGitPathAndNoBranchName() {
        String[] expectedCommand = {"/usr/bin/git", "clone", "--single-branch", "git://path", "/code/"};
        String expectedWorkingDir = "/tmp/";
        Mockito.when(buildEnvironment.getProperty(BUILD_PROP_GIT_PATH)).thenReturn("git://path");
        Mockito.when(buildEnvironment.getProperty(BUILD_PROP_GIT_BRANCH)).thenThrow(new PropertyMissingException(BUILD_PROP_GIT_BRANCH));
        setupFakeGitCheckout("/code/");
        subject.performTask(buildEnvironment);
        Mockito.verify(taskCommandHandler, Mockito.times(1)).executeCommand(expectedCommand, expectedWorkingDir);
        Mockito.verify(buildEnvironment, Mockito.times(1)).addProperty(BUILD_PROP_GIT_BRANCH, "feature/sonar_branch_awareness");
    }

    @Test
    public void testGitCheckoutWithNoGitPathAndNoBranchName() {
        String[] expectedCommand = {"/usr/bin/git", "branch"};
        String expectedWorkingDir = "/runpath/";
        Mockito.when(buildEnvironment.getProperty(BUILD_PROP_GIT_PATH)).thenThrow(new PropertyMissingException(BUILD_PROP_GIT_BRANCH));
        Mockito.when(buildEnvironment.getProperty(BUILD_PROP_GIT_BRANCH)).thenThrow(new PropertyMissingException(BUILD_PROP_GIT_BRANCH));
        setupFakeGitCheckout(expectedWorkingDir);
        subject.performTask(buildEnvironment);
        Mockito.verify(taskCommandHandler, Mockito.times(1)).executeCommand(expectedCommand, expectedWorkingDir);
        Mockito.verify(buildEnvironment, Mockito.times(1)).addProperty(BUILD_PROP_GIT_BRANCH, "feature/sonar_branch_awareness");
    }

    private void setupFakeGitCheckout(String runPath) {
        String gitBranchCommandOutput = "develop\n" +
                "* feature/sonar_branch_awareness\n" +
                "fix/cleanup\n" +
                "help\n" +
                "master";
        InputStream fakeInputStream = new ByteArrayInputStream(gitBranchCommandOutput.getBytes(StandardCharsets.UTF_8));
        Mockito.when(taskCommandHandler.executeCommand(new String[]{"/usr/bin/git", "branch"}, runPath)).thenReturn(fakeInputStream);
        buildEnvironment.codePath = "/code/";
        buildEnvironment.realCIRunPath = "/runpath/";
    }

}