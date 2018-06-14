package com.squarepolka.readyci.tasks.app.ios;

import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.tasks.Task;
import org.springframework.stereotype.Component;

@Component
public class IOSUploadHockeyapp extends Task {

    public static final String TASK_UPLOAD_HOCKEYAPP = "ios_upload_hockeyapp";
    public static final String BUILD_PROP_HOCKEYAPP_TOKEN = "hockappToken";
    public static final String BUILD_PROP_HOCKEYAPP_RELEASE_TAGS = "hockeyappReleaseTags";
    public static final String BUILD_PROP_HOCKEYAPP_RELEASE_NOTES = "hockeyappReleaseNotes";

    @Override
    public String taskIdentifier() {
        return TASK_UPLOAD_HOCKEYAPP;
    }

    @Override
    public void performTask(BuildEnvironment buildEnvironment) {
        String appIdName = buildEnvironment.getProperty(IOSProvisioningProfileRead.BUILD_PROP_APP_ID_NAME);
        String hockappToken = buildEnvironment.getProperty(BUILD_PROP_HOCKEYAPP_TOKEN);
        String releaseTags = buildEnvironment.getProperty(BUILD_PROP_HOCKEYAPP_RELEASE_TAGS, "");
        String releaseNotes = buildEnvironment.getProperty(BUILD_PROP_HOCKEYAPP_RELEASE_NOTES, "");
        String appBinaryPath = String.format("%s/%s.ipa", buildEnvironment.buildPath, appIdName);
        String dsymPath = String.format("%s/app.xcarchive/dSYMs/%s.app.dSYM", buildEnvironment.buildPath, appIdName);
        String dsymPathZip = String.format("%s/app.xcarchive/dSYMs/%s.dsym.zip", buildEnvironment.buildPath, appIdName);

        // Zip the dSYM bundle
        executeCommand(new String[] {"zip",
                "archive",
                "-r",
                dsymPath,
                "--out",
                dsymPathZip});

        // Upload to HockeyApp
        executeCommand(new String[] {"/usr/bin/curl",
            "https://rink.hockeyapp.net/api/2/apps/upload",
            "-H", "X-HockeyAppToken: " + hockappToken,
            "-F", "ipa=@" + appBinaryPath,
            "-F", "dsym=@" + dsymPathZip,
            "-F", "notes=" + releaseNotes,
            "-F", "tags=" + releaseTags,
            "-F", "notes_type=0",               // Textual release notes
            "-F", "status=2",                   // # Make this version available for download
            "-F", "notify=1",                   // Notify users who can install the app
            "-F", "strategy=add",               // Add the build if one with the same build number exists
            "-F", "mandatory=1"                 // Download is mandatory
        }, buildEnvironment.projectPath);

    }

}
