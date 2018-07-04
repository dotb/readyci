package com.squarepolka.readyci.tasks.app.ios.provisioningprofile;

import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.tasks.Task;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class IOSProvisioningProfileInstall extends Task {

    public static final String TASK_IOS_INSTALL_PROFILE = "ios_install_provisioning_profile";

    @Override
    public String taskIdentifier() {
        return TASK_IOS_INSTALL_PROFILE;
    }

    @Override
    public void performTask(BuildEnvironment buildEnvironment) {
        List<String> relativeProfilePaths = buildEnvironment.getProperties(IOSProvisioningProfileRead.BUILD_PROP_PROFILE_PATHS);
        for (String relativeProfilePath : relativeProfilePaths) {
            installProfile(relativeProfilePath, buildEnvironment.projectPath);
        }
    }

    public void installProfile(String relativeProfilePath, String projectPath) {
        String profilePath = String.format("%s/%s", projectPath, relativeProfilePath);
        executeCommand(new String[] {"/usr/bin/open", profilePath});
    }
}
