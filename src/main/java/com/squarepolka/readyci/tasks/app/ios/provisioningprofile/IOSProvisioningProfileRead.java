package com.squarepolka.readyci.tasks.app.ios.provisioningprofile;

import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;
import com.dd.plist.PropertyListParser;
import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.tasks.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;


@Component
public class IOSProvisioningProfileRead extends Task {

    private static final Logger LOGGER = LoggerFactory.getLogger(IOSProvisioningProfileRead.class);
    public static final String TASK_IOS_PROFILE_READ = "ios_provisioning_profile_read";
    public static final String BUILD_PROP_IOS_PROFILES = "provisioningProfileName";
    public static final String BUILD_PROP_APP_ID_NAME = "AppIDName";
    public static final String BUILD_PROP_ORGANISATION_NAME = "organisationName";
    public static final String BUILD_PROP_DEV_TEAM = "devTeam";
    public static final String BUILD_PROP_PROVISIONING_METHOD = "iosProvisioningMethod";
    public static final String BUILD_PROP_PROFILE_PATHS = "iosProfiles";

    public String taskIdentifier() {
        return TASK_IOS_PROFILE_READ;
    }

    public void performTask(BuildEnvironment buildEnvironment) throws Exception {
        List<String> relativeProfilePaths = buildEnvironment.getProperties(BUILD_PROP_PROFILE_PATHS);
        for (String relativeProfilePath : relativeProfilePaths) {
            readProfile(relativeProfilePath, buildEnvironment);
        }

        LOGGER.info("BUILDING\tiOS app\t{} for {} in team {}",
                buildEnvironment.getProperty(BUILD_PROP_APP_ID_NAME),
                buildEnvironment.getProperty(BUILD_PROP_ORGANISATION_NAME),
                buildEnvironment.getProperty(BUILD_PROP_DEV_TEAM));
    }

    public void readProfile(String relativeProfilePath, BuildEnvironment buildEnvironment) throws Exception {
        String profilePath = String.format("%s/%s", buildEnvironment.projectPath, relativeProfilePath);
        InputStream provisioningFileInputStream = decryptProvisioningFile(profilePath);
        readProvisioningInputStream(provisioningFileInputStream, buildEnvironment);
    }

    private InputStream decryptProvisioningFile(String profilePath) {
        LOGGER.debug("Parsing the provisioning profile {}", profilePath);
        return executeCommand(new String[] {"/usr/bin/security", "cms", "-D", "-i", profilePath});
    }

    private void readProvisioningInputStream(InputStream processInputSteam, BuildEnvironment buildEnvironment) throws Exception {
        NSDictionary rootDict = (NSDictionary) PropertyListParser.parse(processInputSteam);
        String appIDName = rootDict.objectForKey("AppIDName").toString();
        String organisationName = rootDict.objectForKey("TeamName").toString();
        NSArray appIdPrefixs = (NSArray) rootDict.objectForKey("ApplicationIdentifierPrefix");
        String devTeam = appIdPrefixs.lastObject().toString();
        ProvisioningProfile profile = extractProvisioningProfile(rootDict, devTeam);

        buildEnvironment.addProperty(BUILD_PROP_APP_ID_NAME, appIDName);
        buildEnvironment.addProperty(BUILD_PROP_ORGANISATION_NAME, organisationName);
        buildEnvironment.addProperty(BUILD_PROP_DEV_TEAM, devTeam);
        buildEnvironment.addProperty(BUILD_PROP_PROVISIONING_METHOD, profile.provisioningType());
        buildEnvironment.addObject(BUILD_PROP_IOS_PROFILES, profile);
    }

    private ProvisioningProfile extractProvisioningProfile(NSDictionary rootDict, String devTeam) {
        String provisioningProfileName = rootDict.objectForKey("Name").toString();
        NSDictionary entitlementsDict = (NSDictionary) rootDict.objectForKey("Entitlements");
        String fullBundleId = entitlementsDict.objectForKey("application-identifier").toString();
        String bundleId = removeTeamFromBundleId(fullBundleId, devTeam);
        NSObject provisionedDevices = rootDict.objectForKey("ProvisionedDevices");
        boolean hasProvisionedDevices = provisionedDevices != null;
        ProvisioningProfile profile = new ProvisioningProfile(provisioningProfileName, bundleId, hasProvisionedDevices);
        return profile;
    }

    private String removeTeamFromBundleId(String bundleId, String teamId) {
        return bundleId.replace(String.format("%s.", teamId), "");
    }

}
