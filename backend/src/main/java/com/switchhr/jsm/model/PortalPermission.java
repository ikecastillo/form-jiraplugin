package com.switchhr.jsm.model;

import net.java.ao.Entity;
import net.java.ao.Preload;
import net.java.ao.schema.Indexed;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.StringLength;
import net.java.ao.schema.Table;

@Table("PORTAL_PERMISSION")
@Preload
public interface PortalPermission extends Entity {

    String PERMISSION_VIEW = "VIEW";
    String PERMISSION_ADMIN = "ADMIN";

    @NotNull
    PortalConfiguration getPortalConfiguration();
    void setPortalConfiguration(PortalConfiguration portalConfiguration);

    @NotNull
    @Indexed
    @StringLength(100)
    String getPortalId();
    void setPortalId(String portalId);

    @NotNull
    @StringLength(255)
    String getPrincipal();
    void setPrincipal(String principal);

    @NotNull
    @StringLength(50)
    String getPrincipalType();
    void setPrincipalType(String principalType);

    @NotNull
    @StringLength(50)
    String getPermission();
    void setPermission(String permission);

    @NotNull
    boolean isActive();
    void setActive(boolean active);
}
