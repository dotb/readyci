package com.squarepolka.readyci.tasks.realci;

import com.squarepolka.readyci.configuration.PipelineConfiguration;
import com.squarepolka.readyci.configuration.ReadyCIConfiguration;
import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.tasks.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Component
public class ConfigurationLoad extends Task {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationLoad.class);
    public static final String TASK_CONFIGURATION_LOAD = "readyci_configuration_load";
    public static final String TASK_CONFIGURATION_FILE_NAME = "readyci.yml";

    @Override
    public String taskIdentifier() {
        return TASK_CONFIGURATION_LOAD;
    }

    @Override
    public void performTask(BuildEnvironment buildEnvironment) {
        File repoConfigurationFile = getRepoConfigurationFile(buildEnvironment);
        if (repoConfigurationFile.exists()) {
            LOGGER.debug(String.format("Loading local repository configuration from %s", TASK_CONFIGURATION_FILE_NAME));
            ReadyCIConfiguration localConfiguration = ReadyCIConfiguration.readConfigurationFile(repoConfigurationFile);
            mergeLocalConfigWithBuildEnvironment(localConfiguration, buildEnvironment);
        } else {
            LOGGER.debug(String.format("Local repository configuration %s not found. Repository configuration is not being used.", TASK_CONFIGURATION_FILE_NAME));
        }
    }
    
    private File getRepoConfigurationFile(BuildEnvironment buildEnvironment) {
        String localConfigurationPath = String.format("%s/%s", buildEnvironment.projectPath, TASK_CONFIGURATION_FILE_NAME);
        return new File(localConfigurationPath);
    }

    private void mergeLocalConfigWithBuildEnvironment(ReadyCIConfiguration localConfiguration, BuildEnvironment buildEnvironment) {
        String pipelineName = buildEnvironment.pipelineName;
        PipelineConfiguration repoPipelineConf = localConfiguration.getPipeline(pipelineName);
        buildEnvironment.getProjectFolderFromConfiguration(repoPipelineConf);
        buildEnvironment.configureProjectPath();
        buildEnvironment.setBuildParameters(repoPipelineConf);

        List<Task> configuredTasks = taskRunner.taskRunnerFactory.createTaskListFromConfig(repoPipelineConf.tasks);
        LOGGER.debug(String.format("Loaded %s tasks from the repository configuration %s", configuredTasks.size(), TASK_CONFIGURATION_FILE_NAME));
        taskRunner.setConfiguredTasks(configuredTasks);


    }
}
