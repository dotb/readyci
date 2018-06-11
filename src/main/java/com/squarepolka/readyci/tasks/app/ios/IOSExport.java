package com.squarepolka.readyci.tasks.app.ios;

import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.tasks.Task;
import org.springframework.stereotype.Component;

@Component
public class IOSExport extends Task {
    @Override
    public String taskIdentifier() {
        return "ios_export";
    }

    @Override
    public void performTask(BuildEnvironment buildEnvironment) throws Exception {
        String archivePath = String.format("%s/app.xcarchive", buildEnvironment.buildPath);
        String exportOptionsPath = String.format("%s/exportOptions.plist", buildEnvironment.buildPath);
        String exportPath = buildEnvironment.buildPath;

        executeCommand(String.format("/usr/bin/xcodebuild " +
                "-exportArchive " +
                "-archivePath %s " +
                "-exportOptionsPlist %s " +
                "-exportPath %s ", archivePath, exportOptionsPath, exportPath));

    }
}
