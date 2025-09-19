package com.switchhr.jsm.service;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.switchhr.jsm.config.PortalConfigurationDTO;
import com.switchhr.jsm.config.PortalPermissionsConfig;
import com.switchhr.jsm.model.PortalConfiguration;
import com.switchhr.jsm.model.PortalPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@Scanned
public class PortalPermissionService {

    private final ActiveObjects activeObjects;
    private final PortalConfigurationService configurationService;
    private final GroupManager groupManager;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public PortalPermissionService(@ComponentImport ActiveObjects activeObjects,
                                   @ComponentImport GroupManager groupManager,
                                   PortalConfigurationService configurationService) {
        this.activeObjects = activeObjects;
        this.configurationService = configurationService;
        this.groupManager = groupManager;
    }

    public boolean hasPortalAccess(ApplicationUser user, String portalId) {
        Optional<PortalConfiguration> configOpt = configurationService.getPortalConfiguration(portalId);
        if (configOpt.isEmpty()) {
            return false;
        }

        PortalConfiguration portalConfiguration = configOpt.get();
        PortalPermissionsConfig permissionsConfig = readPermissions(portalConfiguration);

        if (permissionsConfig != null && Boolean.TRUE.equals(permissionsConfig.getPublicAccess())) {
            return true;
        }

        if (user == null) {
            return false;
        }

        PortalPermission[] storedPermissions = activeObjects.find(PortalPermission.class,
            "PORTAL_ID = ? AND ACTIVE = ?", portalId, true);

        if (storedPermissions.length == 0) {
            return permissionsConfig != null && isUserInGroupList(user, permissionsConfig.getAllowedGroups());
        }

        return Arrays.stream(storedPermissions)
            .filter(permission -> PortalPermission.PERMISSION_VIEW.equals(permission.getPermission())
                || PortalPermission.PERMISSION_ADMIN.equals(permission.getPermission()))
            .anyMatch(permission -> userMatchesPrincipal(user, permission));
    }

    public boolean hasPortalAdminAccess(ApplicationUser user, String portalId) {
        if (user == null) {
            return false;
        }

        PortalPermission[] storedPermissions = activeObjects.find(PortalPermission.class,
            "PORTAL_ID = ? AND PERMISSION = ? AND ACTIVE = ?",
            portalId, PortalPermission.PERMISSION_ADMIN, true);
        if (storedPermissions.length > 0) {
            return Arrays.stream(storedPermissions).anyMatch(permission -> userMatchesPrincipal(user, permission));
        }

        Optional<PortalConfiguration> configOpt = configurationService.getPortalConfiguration(portalId);
        if (configOpt.isEmpty()) {
            return false;
        }

        PortalPermissionsConfig permissionsConfig = readPermissions(configOpt.get());
        return permissionsConfig != null && isUserInGroupList(user, permissionsConfig.getAdminGroups());
    }

    private boolean userMatchesPrincipal(ApplicationUser user, PortalPermission permission) {
        if (permission == null || permission.getPrincipal() == null) {
            return false;
        }
        if (!"GROUP".equalsIgnoreCase(permission.getPrincipalType())) {
            return false;
        }
        return groupManager != null && groupManager.isUserInGroup(user, permission.getPrincipal());
    }

    private boolean isUserInGroupList(ApplicationUser user, List<String> groups) {
        if (groups == null || groups.isEmpty() || groupManager == null) {
            return false;
        }
        return groups.stream().anyMatch(group -> groupManager.isUserInGroup(user, group));
    }

    private PortalPermissionsConfig readPermissions(PortalConfiguration config) {
        if (config == null || config.getConfigurationJson() == null) {
            return null;
        }
        try {
            PortalConfigurationDTO dto = objectMapper.readValue(config.getConfigurationJson(), PortalConfigurationDTO.class);
            return dto.getPermissions();
        } catch (IOException e) {
            return null;
        }
    }
}
