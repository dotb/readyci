package com.squarepolka.readyci.tasks.app.ios;

import com.dd.plist.NSDictionary;
import com.dd.plist.PropertyListParser;
import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.tasks.Task;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class IOSIncrementBuildNumber extends Task {

    public static final String TASK_IOS_INCREMENT_BUILD_NUMBER = "ios_increment_build_number";
    private static final String CFBUNDLEVERSION = "CFBundleVersion";

    @Override
    public String taskIdentifier() {
        return TASK_IOS_INCREMENT_BUILD_NUMBER;
    }

    @Override
    public void performTask(BuildEnvironment buildEnvironment) throws Exception {

        String codePath = buildEnvironment.codePath;
        String relativepListPath = buildEnvironment.getProperty("infoPlistPath");
        String infoPlistPath = String.format("%s/%s", codePath, relativepListPath);

        NSDictionary infoDict = getInfoPlistDict(infoPlistPath);
        Integer buildNumber = getCurrentBuildNumber(infoDict);
        buildNumber = new Integer(buildNumber.intValue() + 1);
        updateNewBuildNumber(infoDict, infoPlistPath, buildNumber);
    }

    public NSDictionary getInfoPlistDict(String infoPlistPath) throws Exception {
        File infoPlistFile = new File(infoPlistPath);
        return (NSDictionary) PropertyListParser.parse(infoPlistFile);
    }

    public Integer getCurrentBuildNumber(NSDictionary infoDict) {
        String buildNumberStr = infoDict.objectForKey(CFBUNDLEVERSION).toString();
        Integer buildNumber = new Integer(buildNumberStr);
        return buildNumber;
    }

    public void updateNewBuildNumber(NSDictionary infoDict, String infoPlistPath, Integer buildNumber) throws Exception {
        File infoPlistFile = new File(infoPlistPath);
        infoDict.put(CFBUNDLEVERSION, buildNumber.toString());
        PropertyListParser.saveAsXML(infoDict, infoPlistFile);
    }
}
