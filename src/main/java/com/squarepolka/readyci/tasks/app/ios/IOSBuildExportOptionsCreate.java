package com.squarepolka.readyci.tasks.app.ios;

import com.dd.plist.NSDictionary;
import com.dd.plist.PropertyListParser;
import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.tasks.Task;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class IOSBuildExportOptionsCreate extends Task {

    public static final String TASK_IOS_EXPORT_OPTIONS_CREATE = "ios_export_options_create";

    @Override
    public String taskIdentifier() {
        return TASK_IOS_EXPORT_OPTIONS_CREATE;
    }

    @Override
    public void performTask(BuildEnvironment buildEnvironment) throws Exception {
        String exportOptionsPath = String.format("%s/exportOptions.plist", buildEnvironment.projectPath);
        String appName = buildEnvironment.getProperty(IOSProvisioningProfileRead.BUILD_PROP_APP_NAME);
        String devTeam = buildEnvironment.getProperty(IOSProvisioningProfileRead.BUILD_PROP_DEV_TEAM);
        String bundleId = buildEnvironment.getProperty(IOSProvisioningProfileRead.BUILD_PROP_BUNDLE_ID);
        String provisioningMethod = buildEnvironment.getProperty(IOSProvisioningProfileRead.BUILD_PROP_PROVISIONING_METHOD);

        createExportOptionsFile(devTeam, bundleId, appName, exportOptionsPath, provisioningMethod);
    }

    private void createExportOptionsFile(String devTeam, String bundleId, String appName, String exportOptionsPath, String provisioningMethod) throws IOException {
        NSDictionary rootDict = new NSDictionary();
        rootDict.put("compileBitcode", true);
        rootDict.put("stripSwiftSymbols", true);
        rootDict.put("method", provisioningMethod);
        rootDict.put("signingCertificate","iPhone Distribution");
        rootDict.put("signingStyle", "manual");
        rootDict.put("thinning","<none>");
        rootDict.put("teamID", devTeam);
        NSDictionary provisioningProfilesDict = new NSDictionary();
        provisioningProfilesDict.put(bundleId, appName);
        rootDict.put("provisioningProfiles", provisioningProfilesDict);

        File exportOptionsFile = new File(exportOptionsPath);
        PropertyListParser.saveAsXML(rootDict, exportOptionsFile);
    }
}

