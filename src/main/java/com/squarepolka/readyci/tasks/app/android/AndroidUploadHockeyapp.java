
package com.squarepolka.readyci.tasks.app.android;


import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.tasks.Task;
import org.springframework.stereotype.Component;

@Component
public class AndroidUploadHockeyapp extends Task {


    public static final String TASK_UPLOAD_HOCKEYAPP = "android_upload_hockeyapp";
    public static final String BUILD_PROP_HOCKEYAPP_TOKEN = "hockappToken";
    public static final String BUILD_PROP_HOCKEYAPP_RELEASE_TAGS = "hockeyappReleaseTags";
    public static final String BUILD_PROP_HOCKEYAPP_RELEASE_NOTES = "hockeyappReleaseNotes";

    @Override
    public String taskIdentifier() {
        return TASK_UPLOAD_HOCKEYAPP;
    }

    @Override
    public void performTask(BuildEnvironment buildEnvironment) {
      
        String scheme = buildEnvironment.getProperty(AndroidSignApp.BUILD_PROP_SCHEME);
        String hockappToken = buildEnvironment.getProperty(BUILD_PROP_HOCKEYAPP_TOKEN);
        String releaseTags = buildEnvironment.getProperty(BUILD_PROP_HOCKEYAPP_RELEASE_TAGS, "");
        String releaseNotes = buildEnvironment.getProperty(BUILD_PROP_HOCKEYAPP_RELEASE_NOTES, "");
        String appBinaryPath = String.format("%s/app/build/outputs/apk/%s/app-%s.apk", buildEnvironment.projectPath, scheme.toLowerCase(), scheme.toLowerCase());

        // Upload to HockeyApp
        executeCommand(new String[] {"/usr/bin/curl",
                "https://rink.hockeyapp.net/api/2/apps/upload",
                "-H", "X-HockeyAppToken: " + hockappToken,
                "-F", "ipa=@" + appBinaryPath,
                "-F", "notes=" + releaseNotes,
                "-F", "tags=" + releaseTags,
                "-F", "notes_type=0",               // Textual release notes
                "-F", "status=2",                   // Make this version available for download
                "-F", "notify=1",                   // Notify users who can install the app
                "-F", "strategy=add",               // Add the build if one with the same build number exists
                "-F", "mandatory=1"                 // Download is mandatory
        }, buildEnvironment.projectPath);
    }

}