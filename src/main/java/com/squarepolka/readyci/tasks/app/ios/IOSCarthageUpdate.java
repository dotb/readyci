package com.squarepolka.readyci.tasks.app.ios;

import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.tasks.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class IOSCarthageUpdate extends Task {
    private static final Logger LOGGER = LoggerFactory.getLogger(IOSCarthageUpdate.class);

    @Override
    public String taskIdentifier() {
        return "ios_carthage_update";
    }

    @Override
    public void performTask(BuildEnvironment buildEnvironment) {
        executeCommand(new String[] {"/usr/local/bin/carthage", "update", "--platform iOS"}, buildEnvironment.projectPath);
    }
}
