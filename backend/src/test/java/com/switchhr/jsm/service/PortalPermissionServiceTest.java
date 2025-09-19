package com.switchhr.jsm.service;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.user.ApplicationUser;
import com.switchhr.jsm.model.PortalConfiguration;
import com.switchhr.jsm.model.PortalPermission;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PortalPermissionServiceTest {

    private ActiveObjects activeObjects;
    private PortalConfigurationService configurationService;
    private GroupManager groupManager;
    private PortalPermissionService permissionService;

    @Before
    public void setUp() {
        activeObjects = Mockito.mock(ActiveObjects.class);
        configurationService = Mockito.mock(PortalConfigurationService.class);
        groupManager = Mockito.mock(GroupManager.class);
        permissionService = new PortalPermissionService(activeObjects, groupManager, configurationService);
    }

    @Test
    public void hasPortalAccess_returnsTrueForPublicPortal() {
        PortalConfiguration config = Mockito.mock(PortalConfiguration.class);
        Mockito.when(config.getConfigurationJson()).thenReturn("{\"permissions\":{\"publicAccess\":true}}");
        Mockito.when(configurationService.getPortalConfiguration("public"))
            .thenReturn(Optional.of(config));
        Mockito.when(activeObjects.find(Mockito.eq(PortalPermission.class), Mockito.anyString(), Mockito.any(), Mockito.any()))
            .thenReturn(new PortalPermission[0]);

        boolean result = permissionService.hasPortalAccess(null, "public");
        assertTrue(result);
    }

    @Test
    public void hasPortalAccess_deniesWhenUserNotInAllowedGroups() {
        PortalConfiguration config = Mockito.mock(PortalConfiguration.class);
        Mockito.when(config.getConfigurationJson())
            .thenReturn("{\"permissions\":{\"publicAccess\":false,\"allowedGroups\":[\"jira-users\"]}}");
        Mockito.when(configurationService.getPortalConfiguration("secure"))
            .thenReturn(Optional.of(config));
        Mockito.when(activeObjects.find(Mockito.eq(PortalPermission.class), Mockito.anyString(), Mockito.any(), Mockito.any()))
            .thenReturn(new PortalPermission[0]);

        ApplicationUser user = Mockito.mock(ApplicationUser.class);
        Mockito.when(groupManager.isUserInGroup(user, "jira-users")).thenReturn(false);

        boolean result = permissionService.hasPortalAccess(user, "secure");
        assertFalse(result);
    }

    @Test
    public void hasPortalAdminAccess_usesStoredPermissions() {
        PortalConfiguration config = Mockito.mock(PortalConfiguration.class);
        Mockito.when(config.getConfigurationJson())
            .thenReturn("{\"permissions\":{\"publicAccess\":false,\"adminGroups\":[\"hr-admins\"]}}");
        Mockito.when(configurationService.getPortalConfiguration("admin"))
            .thenReturn(Optional.of(config));

        PortalPermission permission = Mockito.mock(PortalPermission.class);
        Mockito.when(permission.getPrincipalType()).thenReturn("GROUP");
        Mockito.when(permission.getPrincipal()).thenReturn("hr-admins");
        Mockito.when(permission.getPermission()).thenReturn(PortalPermission.PERMISSION_ADMIN);

        Mockito.when(activeObjects.find(Mockito.eq(PortalPermission.class),
            Mockito.contains("PERMISSION = ?"), Mockito.any(), Mockito.any(), Mockito.any()))
            .thenReturn(new PortalPermission[] { permission });

        ApplicationUser user = Mockito.mock(ApplicationUser.class);
        Mockito.when(groupManager.isUserInGroup(user, "hr-admins")).thenReturn(true);

        boolean result = permissionService.hasPortalAdminAccess(user, "admin");
        assertTrue(result);
    }
}
