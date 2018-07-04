package com.squarepolka.readyci.tasks.app.ios.provisioningprofile;

public class ProvisioningProfile {
    public String name;
    public String bundleId;
    public boolean hasProvisionedDevices;

    public ProvisioningProfile(String name, String bundleId, boolean hasProvisionedDevices) {
        this.name = name;
        this.bundleId = bundleId;
        this.hasProvisionedDevices = hasProvisionedDevices;
    }

    public String provisioningType() {
        if (hasProvisionedDevices) {
            return "ad-hoc";
        } else {
            return "app-store";
        }
    }
}
