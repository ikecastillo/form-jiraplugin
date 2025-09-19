package com.switchhr.jsm.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

public class ComponentInstanceConfig {

    @NotBlank
    @JsonProperty("instanceId")
    private String instanceId;

    @NotBlank
    @JsonProperty("componentId")
    private String componentId;

    @NotNull
    @Valid
    @JsonProperty("position")
    private LayoutPosition position;

    @NotNull
    @JsonProperty("configuration")
    private Map<String, Object> configuration;

    @JsonProperty("conditions")
    private List<DisplayCondition> conditions;

    @JsonProperty("permissions")
    private List<String> permissions;

    @NotNull
    @JsonProperty("enabled")
    private Boolean enabled = Boolean.TRUE;

    @NotNull
    @JsonProperty("sortOrder")
    private Integer sortOrder = 0;

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getComponentId() {
        return componentId;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }

    public LayoutPosition getPosition() {
        return position;
    }

    public void setPosition(LayoutPosition position) {
        this.position = position;
    }

    public Map<String, Object> getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Map<String, Object> configuration) {
        this.configuration = configuration;
    }

    public List<DisplayCondition> getConditions() {
        return conditions;
    }

    public void setConditions(List<DisplayCondition> conditions) {
        this.conditions = conditions;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}
