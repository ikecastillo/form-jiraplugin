package com.switchhr.jsm.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

public class PortalConfigurationDTO {

    @NotBlank
    @JsonProperty("portalId")
    private String portalId;

    @NotBlank
    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @NotNull
    @Valid
    @JsonProperty("branding")
    private BrandingConfig branding;

    @NotNull
    @Valid
    @JsonProperty("layout")
    private LayoutConfig layout;

    @NotNull
    @Valid
    @JsonProperty("components")
    private List<ComponentInstanceConfig> components;

    @NotNull
    @Valid
    @JsonProperty("permissions")
    private PortalPermissionsConfig permissions;

    @NotNull
    @Valid
    @JsonProperty("jiraIntegration")
    private JiraIntegrationConfig jiraIntegration;

    @JsonProperty("customSettings")
    private Map<String, Object> customSettings;

    public String getPortalId() {
        return portalId;
    }

    public void setPortalId(String portalId) {
        this.portalId = portalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BrandingConfig getBranding() {
        return branding;
    }

    public void setBranding(BrandingConfig branding) {
        this.branding = branding;
    }

    public LayoutConfig getLayout() {
        return layout;
    }

    public void setLayout(LayoutConfig layout) {
        this.layout = layout;
    }

    public List<ComponentInstanceConfig> getComponents() {
        return components;
    }

    public void setComponents(List<ComponentInstanceConfig> components) {
        this.components = components;
    }

    public PortalPermissionsConfig getPermissions() {
        return permissions;
    }

    public void setPermissions(PortalPermissionsConfig permissions) {
        this.permissions = permissions;
    }

    public JiraIntegrationConfig getJiraIntegration() {
        return jiraIntegration;
    }

    public void setJiraIntegration(JiraIntegrationConfig jiraIntegration) {
        this.jiraIntegration = jiraIntegration;
    }

    public Map<String, Object> getCustomSettings() {
        return customSettings;
    }

    public void setCustomSettings(Map<String, Object> customSettings) {
        this.customSettings = customSettings;
    }
}
