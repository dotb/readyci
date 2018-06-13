package com.squarepolka.readyci.tasks.app.ios;

import com.dd.plist.NSDictionary;
import com.dd.plist.PropertyListParser;
import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.tasks.Task;
import org.springframework.stereotype.Component;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class IOSIncrementBuildNumber extends Task {

    public static final String TASK_IOS_INCREMENT_BUILD_NUMBER = "ios_increment_build_number";

    @Override
    public String taskIdentifier() {
        return TASK_IOS_INCREMENT_BUILD_NUMBER;
    }

    @Override
    public void performTask(BuildEnvironment buildEnvironment) throws Exception {

        String buildPath = buildEnvironment.buildPath;
        String relativepListPath = buildEnvironment.buildParameters.get("infoPlistPath");
        String infoPlistPath = String.format("%s/%s", buildPath, relativepListPath);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddhhmm");
        String formattedDate = simpleDateFormat.format(new Date());

        File infoPlistFile = new File(infoPlistPath);
        NSDictionary rootDict = (NSDictionary) PropertyListParser.parse(infoPlistFile);
        rootDict.put("CFBundleVersion", formattedDate);
        PropertyListParser.saveAsXML(rootDict, infoPlistFile);
    }
}
