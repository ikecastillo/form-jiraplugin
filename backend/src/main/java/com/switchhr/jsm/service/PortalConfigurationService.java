package com.switchhr.jsm.service;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.switchhr.jsm.config.ComponentInstanceConfig;
import com.switchhr.jsm.config.JiraIntegrationConfig;
import com.switchhr.jsm.config.PortalConfigurationDTO;
import com.switchhr.jsm.config.PortalPermissionsConfig;
import com.switchhr.jsm.model.ComponentInstance;
import com.switchhr.jsm.model.PortalConfiguration;
import com.switchhr.jsm.model.PortalPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class PortalConfigurationService {

    private final ActiveObjects activeObjects;
    private final ConfigurationValidationService validationService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public PortalConfigurationService(@ComponentImport ActiveObjects activeObjects,
                                      ConfigurationValidationService validationService) {
        this.activeObjects = activeObjects;
        this.validationService = validationService;
    }

    @Nonnull
    public PortalConfiguration createPortalConfiguration(String portalId,
                                                         String name,
                                                         String configurationJson,
                                                         String userKey) {
        PortalConfigurationDTO parsedConfig = validateAndParse(configurationJson);

        return activeObjects.executeInTransaction(() -> {
            PortalConfiguration config = activeObjects.create(PortalConfiguration.class);
            Date now = new Date();
            populateConfigurationEntity(config, portalId, name, parsedConfig, configurationJson, userKey, now);
            config.setCreatedDate(now);
            config.setCreatedBy(userKey);
            config.save();

            syncComponentInstances(config, parsedConfig);
            syncPermissions(config, parsedConfig);
            return config;
        });
    }

    public void updatePortalConfiguration(String portalId,
                                          String configurationJson,
                                          String userKey) {
        PortalConfigurationDTO parsedConfig = validateAndParse(configurationJson);

        activeObjects.executeInTransaction(() -> {
            PortalConfiguration[] configs = activeObjects.find(PortalConfiguration.class, "PORTAL_ID = ?", portalId);
            if (configs.length == 0) {
                throw new ValidationException("Portal configuration not found for id " + portalId);
            }

            PortalConfiguration config = configs[0];
            populateConfigurationEntity(config, portalId, config.getName(), parsedConfig, configurationJson, userKey, new Date());
            config.save();

            removeExistingComponentInstances(config);
            syncComponentInstances(config, parsedConfig);
            removeExistingPermissions(config);
            syncPermissions(config, parsedConfig);
            return null;
        });
    }

    public void deletePortalConfiguration(String portalId) {
        activeObjects.executeInTransaction(() -> {
            PortalConfiguration[] configs = activeObjects.find(PortalConfiguration.class, "PORTAL_ID = ?", portalId);
            if (configs.length > 0) {
                PortalConfiguration config = configs[0];
                config.setActive(false);
                config.setLastModified(new Date());
                config.save();
            }
            return null;
        });
    }

    @Nonnull
    public Optional<PortalConfiguration> getPortalConfiguration(String portalId) {
        PortalConfiguration[] configs = activeObjects.find(PortalConfiguration.class,
            "PORTAL_ID = ? AND ACTIVE = ?", portalId, true);
        return configs.length > 0 ? Optional.of(configs[0]) : Optional.empty();
    }

    @Nonnull
    public List<PortalConfiguration> getAllActivePortalConfigurations() {
        return Arrays.asList(activeObjects.find(PortalConfiguration.class, "ACTIVE = ?", true));
    }

    private PortalConfigurationDTO validateAndParse(String configurationJson) {
        ValidationResult result = validationService.validateConfiguration(configurationJson);
        if (!result.isValid()) {
            String message = result.getErrors().stream().collect(Collectors.joining("; "));
            throw new ValidationException(message);
        }
        try {
            return objectMapper.readValue(configurationJson, PortalConfigurationDTO.class);
        } catch (JsonProcessingException e) {
            throw new ValidationException("Unable to parse portal configuration: " + e.getMessage());
        }
    }

    private void populateConfigurationEntity(PortalConfiguration config,
                                             String portalId,
                                             String name,
                                             PortalConfigurationDTO parsedConfig,
                                             String configurationJson,
                                             String userKey,
                                             Date modifiedDate) {
        config.setPortalId(portalId);
        config.setName(name);
        config.setDescription(parsedConfig.getDescription());
        config.setConfigurationJson(configurationJson);
        config.setActive(true);
        config.setLastModified(modifiedDate);
        config.setModifiedBy(userKey);
        config.setServiceDeskId(extractServiceDeskId(parsedConfig.getJiraIntegration()));
    }

    private String extractServiceDeskId(JiraIntegrationConfig jiraIntegration) {
        return jiraIntegration != null ? jiraIntegration.getServiceDeskId() : null;
    }

    private void syncComponentInstances(PortalConfiguration config, PortalConfigurationDTO parsedConfig) {
        List<ComponentInstanceConfig> componentConfigs = parsedConfig.getComponents();
        if (componentConfigs == null || componentConfigs.isEmpty()) {
            return;
        }

        componentConfigs.forEach(instanceConfig -> {
            ComponentInstance instance = activeObjects.create(ComponentInstance.class);
            instance.setPortalConfiguration(config);
            instance.setInstanceId(instanceConfig.getInstanceId());
            instance.setComponentId(instanceConfig.getComponentId());
            instance.setInstanceConfiguration(writeAsJson(instanceConfig.getConfiguration()));
            instance.setLayoutPosition(writeAsJson(instanceConfig.getPosition()));
            instance.setSortOrder(instanceConfig.getSortOrder() != null ? instanceConfig.getSortOrder() : 0);
            instance.setEnabled(instanceConfig.getEnabled() == null || instanceConfig.getEnabled());
            instance.save();
        });
    }

    private void syncPermissions(PortalConfiguration config, PortalConfigurationDTO parsedConfig) {
        PortalPermissionsConfig permissions = parsedConfig.getPermissions();
        if (permissions == null) {
            return;
        }

        List<PortalPermission> createdPermissions = new ArrayList<>();
        List<String> allowedGroups = permissions.getAllowedGroups();
        if (allowedGroups != null) {
            allowedGroups.forEach(group -> createdPermissions.add(createPermission(config, group, PortalPermission.PERMISSION_VIEW)));
        }

        List<String> adminGroups = permissions.getAdminGroups();
        if (adminGroups != null) {
            adminGroups.forEach(group -> createdPermissions.add(createPermission(config, group, PortalPermission.PERMISSION_ADMIN)));
        }

        createdPermissions.forEach(PortalPermission::save);
    }

    private PortalPermission createPermission(PortalConfiguration config, String principal, String permission) {
        PortalPermission entity = activeObjects.create(PortalPermission.class);
        entity.setPortalConfiguration(config);
        entity.setPortalId(config.getPortalId());
        entity.setPrincipal(principal);
        entity.setPrincipalType("GROUP");
        entity.setPermission(permission);
        entity.setActive(true);
        return entity;
    }

    private void removeExistingComponentInstances(PortalConfiguration config) {
        ComponentInstance[] existing = config.getComponentInstances();
        if (existing != null && existing.length > 0) {
            activeObjects.delete(existing);
        }
    }

    private void removeExistingPermissions(PortalConfiguration config) {
        PortalPermission[] existing = config.getPermissions();
        if (existing != null && existing.length > 0) {
            activeObjects.delete(existing);
        }
    }

    private String writeAsJson(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new ValidationException("Failed to serialize configuration: " + e.getMessage());
        }
    }
}

