package com.switchhr.jsm.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WorkflowConfig {

    @JsonProperty("enableAutoAssignment")
    private Boolean enableAutoAssignment;

    @JsonProperty("defaultAssignee")
    private String defaultAssignee;

    @JsonProperty("escalationPolicy")
    private String escalationPolicy;

    public Boolean getEnableAutoAssignment() {
        return enableAutoAssignment;
    }

    public void setEnableAutoAssignment(Boolean enableAutoAssignment) {
        this.enableAutoAssignment = enableAutoAssignment;
    }

    public String getDefaultAssignee() {
        return defaultAssignee;
    }

    public void setDefaultAssignee(String defaultAssignee) {
        this.defaultAssignee = defaultAssignee;
    }

    public String getEscalationPolicy() {
        return escalationPolicy;
    }

    public void setEscalationPolicy(String escalationPolicy) {
        this.escalationPolicy = escalationPolicy;
    }
}
