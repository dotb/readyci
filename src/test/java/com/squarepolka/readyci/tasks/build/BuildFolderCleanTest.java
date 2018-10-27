package com.squarepolka.readyci.tasks.build;

import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.tasks.readyci.TaskCommandHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BuildFolderCleanTest {

    @InjectMocks
    private BuildFolderClean subject = new BuildFolderClean();
    @Mock
    private BuildEnvironment buildEnvironment;
    @Mock
    private TaskCommandHandler taskCommandHandler;

    @Test
    public void taskRemovesSpecifiedFolder() {
        String[] expectedCommand = {"rm", "-fR", "/tmp/readyci/random-uuid"};
        String expectedWorkingDir = "/tmp/";
        buildEnvironment.scratchPath = "/tmp/readyci/random-uuid";
        subject.performTask(buildEnvironment);
        Mockito.verify(taskCommandHandler, Mockito.times(1)).executeCommand(expectedCommand, expectedWorkingDir);
    }

    @Test
    public void deleteMustBeWithinScratchFolder() {
        buildEnvironment.scratchPath = "/tmp/";
        subject.performTask(buildEnvironment);
        Mockito.verify(taskCommandHandler, Mockito.times(0)).executeCommand(Mockito.any(String[].class), Mockito.anyString());
    }

    @Test
    public void scratchPathMustNotBeEmpty() {
        buildEnvironment.scratchPath = "";
        subject.performTask(buildEnvironment);
        Mockito.verify(taskCommandHandler, Mockito.times(0)).executeCommand(Mockito.any(String[].class), Mockito.anyString());
    }

}
