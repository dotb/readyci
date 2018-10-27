package com.squarepolka.readyci.tasks.code;

import com.squarepolka.readyci.configuration.PipelineConfiguration;
import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.InputStream;
import java.util.HashMap;

import static com.squarepolka.readyci.tasks.code.GitCheckout.BUILD_PROP_GIT_BRANCH;
import static com.squarepolka.readyci.tasks.code.GitCheckout.BUILD_PROP_GIT_PATH;
import static org.junit.Assert.assertEquals;

class GitCheckoutTest {

    private GitCheckout subject;
    private BuildEnvironment buildEnvironment;
    private PipelineConfiguration pipelineConfiguration;
    private InputStream inputStream;


    @BeforeEach
    void setUp() {
        pipelineConfiguration = Mockito.mock(PipelineConfiguration.class);
        pipelineConfiguration.parameters = Mockito.mock(HashMap.class);
        buildEnvironment = Mockito.mock(BuildEnvironment.class);
        inputStream = Mockito.mock(InputStream.class);
        subject = Mockito.spy(GitCheckout.class);
        Mockito.when(subject.executeCommand(Mockito.any(String[].class), Mockito.anyString())).thenReturn(inputStream);
    }

    @Test
    public void checkTaskIdentifier() {
        String taskName = subject.taskIdentifier();
        assertEquals("The GitCheckout task identifier is correct", "checkout_git", taskName);
    }

    @Test
    public void checkGitCheckoutWorksWithGitPathAndBranchName() {
        String[] expectedCommand = {""};
        String expectedWorkingDir = "/tmp";
        Mockito.when(pipelineConfiguration.parameters.get(BUILD_PROP_GIT_PATH)).thenReturn("git://path");
        Mockito.when(pipelineConfiguration.parameters.get(BUILD_PROP_GIT_BRANCH)).thenReturn("git_branch");
        subject.performTask(buildEnvironment);
        Mockito.verify(subject, Mockito.times(1)).executeCommand(expectedCommand, expectedWorkingDir);
    }
}