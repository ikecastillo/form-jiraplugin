package com.switchhr.jsm.model;

import net.java.ao.Entity;
import net.java.ao.Preload;
import net.java.ao.schema.Indexed;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.StringLength;
import net.java.ao.schema.Table;

import java.util.Date;

@Table("PORTAL_CONFIG")
@Preload
public interface PortalConfiguration extends Entity {

    @NotNull
    @Indexed
    @StringLength(100)
    String getPortalId();
    void setPortalId(String portalId);

    @NotNull
    @StringLength(255)
    String getName();
    void setName(String name);

    @StringLength(1000)
    String getDescription();
    void setDescription(String description);

    @NotNull
    @StringLength(StringLength.UNLIMITED)
    String getConfigurationJson();
    void setConfigurationJson(String configurationJson);

    @StringLength(100)
    String getServiceDeskId();
    void setServiceDeskId(String serviceDeskId);

    @NotNull
    boolean isActive();
    void setActive(boolean active);

    @NotNull
    Date getCreatedDate();
    void setCreatedDate(Date createdDate);

    @NotNull
    Date getLastModified();
    void setLastModified(Date lastModified);

    @StringLength(100)
    String getCreatedBy();
    void setCreatedBy(String createdBy);

    @StringLength(100)
    String getModifiedBy();
    void setModifiedBy(String modifiedBy);

    ComponentInstance[] getComponentInstances();

    PortalPermission[] getPermissions();
}
