package com.switchhr.jsm.model;

import net.java.ao.Entity;
import net.java.ao.Preload;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.StringLength;
import net.java.ao.schema.Table;

@Table("COMPONENT_INST")
@Preload
public interface ComponentInstance extends Entity {

    @NotNull
    @StringLength(100)
    String getInstanceId();
    void setInstanceId(String instanceId);

    @NotNull
    @StringLength(100)
    String getComponentId();
    void setComponentId(String componentId);

    @NotNull
    PortalConfiguration getPortalConfiguration();
    void setPortalConfiguration(PortalConfiguration portalConfiguration);

    @NotNull
    @StringLength(StringLength.UNLIMITED)
    String getInstanceConfiguration();
    void setInstanceConfiguration(String instanceConfiguration);

    @NotNull
    @StringLength(StringLength.UNLIMITED)
    String getLayoutPosition();
    void setLayoutPosition(String layoutPosition);

    @NotNull
    int getSortOrder();
    void setSortOrder(int sortOrder);

    @NotNull
    boolean isEnabled();
    void setEnabled(boolean enabled);
}
