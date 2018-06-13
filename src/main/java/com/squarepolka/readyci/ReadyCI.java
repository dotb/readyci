package com.squarepolka.readyci;

import com.squarepolka.readyci.configuration.PipelineConfiguration;
import com.squarepolka.readyci.configuration.ReadyCIConfiguration;
import com.squarepolka.readyci.taskrunner.TaskRunner;
import com.squarepolka.readyci.taskrunner.TaskRunnerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import static java.lang.System.exit;


@SpringBootApplication
@EnableScheduling
@EnableAsync
public class ReadyCI implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReadyCI.class);
    private TaskRunnerFactory taskRunnerFactory;

    @Autowired
    public ReadyCI(TaskRunnerFactory taskRunnerFactory) {
        this.taskRunnerFactory = taskRunnerFactory;
    }

    public static void main(String[] arguments) {

        ReadyCIConfiguration readyCIConfiguration = ReadyCIConfiguration.instance();
        readyCIConfiguration.handleInputParameters(arguments);

        SpringApplication.run(ReadyCI.class, arguments);
    }

    @Override
    public void run(String... args) throws Exception {
        ReadyCIConfiguration readyCIConfiguration = ReadyCIConfiguration.instance();
        if (readyCIConfiguration.isServerMode) {
            LOGGER.info("Ready CI is in server mode");
        } else {
            LOGGER.info("Ready CI is in command-line mode");
            runCommandLinePipeline();
            exit(0);
        }
    }

    private void runCommandLinePipeline() {
        ReadyCIConfiguration readyCIConfiguration = ReadyCIConfiguration.instance();
        PipelineConfiguration pipelineConfiguration = readyCIConfiguration.piplineToRun;
        if (null != pipelineConfiguration) {
            LOGGER.info(String.format("Building pipline %s", pipelineConfiguration.name));
            TaskRunner taskRunner = taskRunnerFactory.createTaskRunner(pipelineConfiguration);
            taskRunner.runTasks();
        } else {
            LOGGER.warn("A command line build was not specified, doing nothing. Give me a pipeline name and I'll make it happen.");
        }
    }
}
