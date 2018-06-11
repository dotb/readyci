package com.squarepolka.readyci.tasks.app.ios;

import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.tasks.Task;
import org.springframework.stereotype.Component;

@Component
public class IOSArchive extends Task {
    @Override
    public String taskIdentifier() {
        return "ios_archive";
    }

    @Override
    public void performTask(BuildEnvironment buildEnvironment) throws Exception {

        String workspace = String.format("%s.xcworkspace", buildEnvironment.buildParameters.get("workspace"));
        String scheme = buildEnvironment.buildParameters.get("target");
        String configuration = buildEnvironment.buildParameters.get("configuration");
        String devTeam = buildEnvironment.buildParameters.get(IOSProvisioningProfileRead.BUILD_PROP_DEV_TEAM);
        String provisioningProfile = buildEnvironment.buildParameters.get(IOSProvisioningProfileRead.BUILD_PROP_PROVISIONING_PROFILE);
        String appName = buildEnvironment.buildParameters.get(IOSProvisioningProfileRead.BUILD_PROP_APP_NAME);

        String bundleId = buildEnvironment.buildParameters.get(IOSProvisioningProfileRead.BUILD_PROP_BUNDLE_ID);

        executeCommand(String.format("/usr/bin/xcodebuild " +
                "DEVELOPMENT_TEAM=%s " +
                "PROVISIONING_PROFILE=%s " +
                "-workspace %s " +
                "-scheme %s " +
                "-sdk iphoneos " +
                "-configuration %s " +
                "-archivePath $buildPath/app.xcarchive " +
                "archive", devTeam, provisioningProfile, workspace, scheme, configuration), buildEnvironment.buildPath);
    }
}
