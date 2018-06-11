package com.squarepolka.readyci.tasks.app.ios;

import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;
import com.dd.plist.PropertyListParser;
import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.tasks.Task;
import com.squarepolka.readyci.tasks.TaskExecuteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;


@Component
public class CompileIOSApp extends Task {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompileIOSApp.class);

    public String taskIdentifier() {
        return "ios_compile_app";
    }

    public String description() {
        return "Compile iOS app";
    }

    public void performTask(BuildEnvironment buildEnvironment) {
        try {
            IOSProvisioningProperties profileProperties = readProvisioningProfile(buildEnvironment);

            LOGGER.info(String.format("BUILDING %s for %s in team %s with identifier %s",
                    profileProperties.appName,
                    profileProperties.organisationName,
                    profileProperties.devTeam,
                    profileProperties.bundleId));
        } catch (Exception e) {
            TaskExecuteException taskExecuteException = new TaskExecuteException(String.format("Exception while performing task %s %s", taskIdentifier(), e.toString()));
            taskExecuteException.setStackTrace(e.getStackTrace());
            throw taskExecuteException;
        }
    }

    private IOSProvisioningProperties readProvisioningProfile(BuildEnvironment buildEnvironment) throws Exception {
        String relativeProfilePath = parameters.get("profilePath");
        String profilePath = String.format("%s/%s", buildEnvironment.buildPath, relativeProfilePath);
        InputStream provisioningFileInputStream = decryptProvisioningFile(profilePath);
        return readProvisioningInputStream(provisioningFileInputStream);
    }

    private InputStream decryptProvisioningFile(String profilePath) {
        LOGGER.debug(String.format("Parsing the provisioning profile %s", profilePath));
        return executeCommandWithOutput(String.format("/usr/bin/security cms -D -i %s", profilePath));
    }

    private IOSProvisioningProperties readProvisioningInputStream(InputStream processInputSteam) throws Exception {
        IOSProvisioningProperties iosProvisioningProperties = new IOSProvisioningProperties();
        NSDictionary rootDict = (NSDictionary) PropertyListParser.parse(processInputSteam);
        iosProvisioningProperties.appName = rootDict.objectForKey("Name").toString();
        iosProvisioningProperties.organisationName = rootDict.objectForKey("TeamName").toString();
        NSArray appIdPrefixs = (NSArray) rootDict.objectForKey("ApplicationIdentifierPrefix");
        iosProvisioningProperties.devTeam = appIdPrefixs.lastObject().toString();
        iosProvisioningProperties.provisioningProfile = rootDict.objectForKey("UUID").toString();
        NSDictionary entitlementsDict = (NSDictionary) rootDict.objectForKey("Entitlements");
        String fullBundleId = entitlementsDict.objectForKey("application-identifier").toString();
        iosProvisioningProperties.bundleId = removeTeamFromBundleId(fullBundleId, iosProvisioningProperties.devTeam);
        return iosProvisioningProperties;
    }

    private String removeTeamFromBundleId(String bundleId, String teamId) {
        return bundleId.replace(String.format("%s.", teamId), "");
    }

}
