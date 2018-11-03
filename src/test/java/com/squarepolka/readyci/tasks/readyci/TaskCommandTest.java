package com.squarepolka.readyci.tasks.readyci;

import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.util.PropertyMissingException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;


@RunWith(MockitoJUnitRunner.class)
public class TaskCommandTest {

    private static final String TEST_COMMAND_SINGLE_STRING = "user=jane";
    private static final String TEST_COMMAND_KEY = "testCommandKey";
    private static final String TEST_BUILD_ENV_KEY_STRING = "testBuildEnvKeyString";
    private static final String TEST_BUILD_ENV_KEY_BOOL_TRUE = "testBuildEnvKeyBoolTrue";
    private static final String TEST_BUILD_ENV_KEY_BOOL_FALSE = "testBuildEnvKeyBoolFalse";
    private static final String TEST_BUILD_ENV_KEY_MISSING = "testBuildEnvKeyMssing";
    private static final String TEST_BUILD_ENV_VALUE_STRING = "testBuildEnvValue";
    private static final String TEST_BUILD_ENV_VALUE_BOOL_TRUE = "true";
    private static final String TEST_BUILD_ENV_VALUE_BOOL_FALSE = "false";

    @InjectMocks
    public TaskCommand subject;

    @Mock
    BuildEnvironment buildEnvironment;

    @Before
    public void setup() {
        Mockito.when(buildEnvironment.getSwitch(TEST_BUILD_ENV_KEY_BOOL_TRUE)).thenReturn(true);
        Mockito.when(buildEnvironment.getSwitch(TEST_BUILD_ENV_VALUE_BOOL_FALSE)).thenReturn(false);
        Mockito.when(buildEnvironment.getProperty(TEST_BUILD_ENV_KEY_STRING)).thenReturn(TEST_BUILD_ENV_VALUE_STRING);
        Mockito.when(buildEnvironment.getProperty(TEST_BUILD_ENV_KEY_MISSING)).thenThrow(PropertyMissingException.class);
        Mockito.when(buildEnvironment.getSwitch(TEST_BUILD_ENV_KEY_MISSING)).thenThrow(PropertyMissingException.class);
    }


    @Test
    public void addStringCommand() {
        subject.addStringCommand(TEST_COMMAND_SINGLE_STRING);
        List commandAndParams = subject.getCommandAndParams();
        assertEquals("A single string command is added properly", TEST_COMMAND_SINGLE_STRING, commandAndParams.get(0));
    }

    @Test
    public void addStringParameter() {
        subject.addStringParameter(TEST_COMMAND_KEY, TEST_BUILD_ENV_KEY_STRING);
        List commandAndParams = subject.getCommandAndParams();
        String expectedCommandString = TEST_COMMAND_KEY + "=" + TEST_BUILD_ENV_VALUE_STRING;
        assertEquals("An environment parameter is added properly", expectedCommandString, commandAndParams.get(0));
    }

    @Test
    public void addStringParameterThatIsMissing() {
        subject.addStringParameter(TEST_COMMAND_KEY, TEST_BUILD_ENV_KEY_MISSING);
        List commandAndParams = subject.getCommandAndParams();
        assertEquals("A missing parameter is not added and the list of commands is empty", 0, commandAndParams.size());
    }

    @Test
    public void addBooleanParameterThatIsTrue() {
        subject.addBooleanParameter(TEST_COMMAND_KEY, TEST_BUILD_ENV_KEY_BOOL_TRUE);
        List commandAndParams = subject.getCommandAndParams();
        String expectedCommandString = TEST_COMMAND_KEY + "=" + TEST_BUILD_ENV_VALUE_BOOL_TRUE;
        assertEquals("An boolean environment parameter set to true is added properly", expectedCommandString, commandAndParams.get(0));
    }

    @Test
    public void addBooleanParameterThatIsFalse() {
        subject.addBooleanParameter(TEST_COMMAND_KEY, TEST_BUILD_ENV_KEY_BOOL_FALSE);
        List commandAndParams = subject.getCommandAndParams();
        String expectedCommandString = TEST_COMMAND_KEY + "=" + TEST_BUILD_ENV_VALUE_BOOL_FALSE;
        assertEquals("An boolean environment parameter set to false is added properly", expectedCommandString, commandAndParams.get(0));
    }

    @Test
    public void addBooleanParameterThatIsMissing() {
        subject.addBooleanParameter(TEST_COMMAND_KEY, TEST_BUILD_ENV_KEY_MISSING);
        List commandAndParams = subject.getCommandAndParams();
        assertEquals("A missing boolean parameter is not added and the list of commands is empty", 0, commandAndParams.size());
    }

}