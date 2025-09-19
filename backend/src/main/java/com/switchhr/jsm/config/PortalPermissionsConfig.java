package com.switchhr.jsm.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import java.util.List;

public class PortalPermissionsConfig {

    @JsonProperty("allowedGroups")
    private List<String> allowedGroups;

    @JsonProperty("adminGroups")
    private List<String> adminGroups;

    @NotNull
    @JsonProperty("publicAccess")
    private Boolean publicAccess = Boolean.FALSE;

    public List<String> getAllowedGroups() {
        return allowedGroups;
    }

    public void setAllowedGroups(List<String> allowedGroups) {
        this.allowedGroups = allowedGroups;
    }

    public List<String> getAdminGroups() {
        return adminGroups;
    }

    public void setAdminGroups(List<String> adminGroups) {
        this.adminGroups = adminGroups;
    }

    public Boolean getPublicAccess() {
        return publicAccess;
    }

    public void setPublicAccess(Boolean publicAccess) {
        this.publicAccess = publicAccess;
    }
}
