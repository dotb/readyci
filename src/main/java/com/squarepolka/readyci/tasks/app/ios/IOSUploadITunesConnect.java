package com.squarepolka.readyci.tasks.app.ios;

import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.tasks.Task;
import org.springframework.stereotype.Component;

@Component
public class IOSUploadITunesConnect extends Task {

    public static final String TASK_UPLOAD_ITUNES_CONNECT = "ios_upload_itunes_connect";

    @Override
    public String taskIdentifier() {
        return TASK_UPLOAD_ITUNES_CONNECT;
    }

    @Override
    public void performTask(BuildEnvironment buildEnvironment) throws Exception {

        String appIdName = buildEnvironment.buildParameters.get(IOSProvisioningProfileRead.BUILD_PROP_APP_ID_NAME);
        String exportPath = String.format("%s/%s.ipa", buildEnvironment.buildPath, appIdName);
        String iTunesUsername = buildEnvironment.buildParameters.get("iTunesUsername");
        String iTunesPassword = buildEnvironment.buildParameters.get("iTunesPassword");

        executeCommand(new String[] {"/Applications/Xcode.app/Contents/Applications/Application Loader.app/Contents/Frameworks/ITunesSoftwareService.framework/Support/altool",
        "--upload-app",
        "-f", exportPath,
        "-u", iTunesUsername,
        "-p", iTunesPassword});
    }
}