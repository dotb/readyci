package com.squarepolka.readyci.tasks.app.ios;

import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.tasks.Task;
import org.springframework.stereotype.Component;

@Component
public class IOSUploadAppleConnect extends Task {

    public static final String TASK_UPLOAD_ITUNES_CONNECT = "ios_upload_itunes_connect";

    @Override
    public String taskIdentifier() {
        return TASK_UPLOAD_ITUNES_CONNECT;
    }

    @Override
    public void performTask(BuildEnvironment buildEnvironment) throws Exception {

        String scheme = buildEnvironment.getProperty(IOSBuildArchive.BUILD_PROP_IOS_SCHEME);
        String exportPath = String.format("%s/%s.ipa", buildEnvironment.scratchPath, scheme);
        String iTunesUsername = buildEnvironment.getProperty("iTunesUsername");
        String iTunesPassword = buildEnvironment.getProperty("iTunesPassword");

        executeCommand(new String[] {"/Applications/Xcode.app/Contents/Applications/Application Loader.app/Contents/Frameworks/ITunesSoftwareService.framework/Support/altool",
        "--upload-app",
        "-f", exportPath,
        "-u", iTunesUsername,
        "-p", iTunesPassword});
    }
}
