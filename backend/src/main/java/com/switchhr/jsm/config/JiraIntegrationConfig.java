package com.switchhr.jsm.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

public class JiraIntegrationConfig {

    @NotBlank
    @JsonProperty("serviceDeskId")
    private String serviceDeskId;

    @JsonProperty("defaultRequestTypeId")
    private String defaultRequestTypeId;

    @NotNull
    @JsonProperty("fieldMappings")
    private Map<String, String> fieldMappings;

    @JsonProperty("workflowConfig")
    private WorkflowConfig workflowConfig;

    @JsonProperty("notificationConfig")
    private NotificationConfig notificationConfig;

    @JsonProperty("customFields")
    private Map<String, Object> customFields;

    @NotNull
    @JsonProperty("enableAutoAssignment")
    private Boolean enableAutoAssignment = Boolean.FALSE;

    @JsonProperty("assignmentRules")
    private List<AssignmentRule> assignmentRules;

    public String getServiceDeskId() {
        return serviceDeskId;
    }

    public void setServiceDeskId(String serviceDeskId) {
        this.serviceDeskId = serviceDeskId;
    }

    public String getDefaultRequestTypeId() {
        return defaultRequestTypeId;
    }

    public void setDefaultRequestTypeId(String defaultRequestTypeId) {
        this.defaultRequestTypeId = defaultRequestTypeId;
    }

    public Map<String, String> getFieldMappings() {
        return fieldMappings;
    }

    public void setFieldMappings(Map<String, String> fieldMappings) {
        this.fieldMappings = fieldMappings;
    }

    public WorkflowConfig getWorkflowConfig() {
        return workflowConfig;
    }

    public void setWorkflowConfig(WorkflowConfig workflowConfig) {
        this.workflowConfig = workflowConfig;
    }

    public NotificationConfig getNotificationConfig() {
        return notificationConfig;
    }

    public void setNotificationConfig(NotificationConfig notificationConfig) {
        this.notificationConfig = notificationConfig;
    }

    public Map<String, Object> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(Map<String, Object> customFields) {
        this.customFields = customFields;
    }

    public Boolean getEnableAutoAssignment() {
        return enableAutoAssignment;
    }

    public void setEnableAutoAssignment(Boolean enableAutoAssignment) {
        this.enableAutoAssignment = enableAutoAssignment;
    }

    public List<AssignmentRule> getAssignmentRules() {
        return assignmentRules;
    }

    public void setAssignmentRules(List<AssignmentRule> assignmentRules) {
        this.assignmentRules = assignmentRules;
    }
}
