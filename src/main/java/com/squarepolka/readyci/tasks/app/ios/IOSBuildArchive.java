package com.squarepolka.readyci.tasks.app.ios;

import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.tasks.Task;
import org.springframework.stereotype.Component;

@Component
public class IOSBuildArchive extends Task {

    public static final String TASK_IOS_ARCHIVE = "ios_archive";
    public static final String BUILD_PROP_IOS_SCHEME = "scheme";

    @Override
    public String taskIdentifier() {
        return TASK_IOS_ARCHIVE;
    }

    @Override
    public void performTask(BuildEnvironment buildEnvironment) {
        String workspace = String.format("%s.xcworkspace", buildEnvironment.getProperty("workspace"));
        String scheme = buildEnvironment.getProperty(BUILD_PROP_IOS_SCHEME);
        String configuration = buildEnvironment.getProperty("configuration");
        String devTeam = buildEnvironment.getProperty(IOSProvisioningProfileRead.BUILD_PROP_DEV_TEAM);
        String archivePath = String.format("%s/app.xcarchive", buildEnvironment.buildPath);

        executeCommand(new String[] {"/usr/bin/xcodebuild",
                "DEVELOPMENT_TEAM=" + devTeam,
                "-workspace", workspace,
                "-scheme", scheme,
                "-sdk", "iphoneos",
                "-configuration", configuration,
                "-archivePath", archivePath,
                "archive"}, buildEnvironment.projectPath);
    }
}
