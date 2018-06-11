package com.squarepolka.readyci.tasks.app.ios;

import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.tasks.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class IOSPodsInstall extends Task {

    private static final Logger LOGGER = LoggerFactory.getLogger(IOSPodsInstall.class);

    @Override
    public String taskIdentifier() {
        return "ios_pod_install";
    }

    @Override
    public void performTask(BuildEnvironment buildEnvironment) {
        executeCommand("/usr/local/bin/pod install", buildEnvironment.buildPath);
    }
}
