package com.squarepolka.readyci.tasks.app.ios;

import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.tasks.Task;
import org.springframework.stereotype.Component;

@Component
public class IOSInstallProvisioningProfile extends Task {
    @Override
    public String taskIdentifier() {
        return "ios_install_provisioning_profile";
    }

    @Override
    public void performTask(BuildEnvironment buildEnvironment) {
        String relativeProfilePath = buildEnvironment.buildParameters.get("profilePath");
        String profilePath = String.format("%s/%s", buildEnvironment.projectPath, relativeProfilePath);
        executeCommand(new String[] {"/usr/bin/open", profilePath});
    }
}
