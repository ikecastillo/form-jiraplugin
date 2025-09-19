package com.switchhr.jsm.model;

import net.java.ao.Entity;
import net.java.ao.Preload;
import net.java.ao.schema.Indexed;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.StringLength;
import net.java.ao.schema.Table;

@Table("COMPONENT_DEF")
@Preload
public interface ComponentDefinition extends Entity {

    @NotNull
    @Indexed
    @StringLength(100)
    String getComponentId();
    void setComponentId(String componentId);

    @NotNull
    @StringLength(255)
    String getName();
    void setName(String name);

    @NotNull
    @StringLength(50)
    String getType();
    void setType(String type);

    @StringLength(1000)
    String getDescription();
    void setDescription(String description);

    @NotNull
    @StringLength(20)
    String getVersion();
    void setVersion(String version);

    @NotNull
    @StringLength(StringLength.UNLIMITED)
    String getConfigurationSchema();
    void setConfigurationSchema(String configurationSchema);

    @StringLength(StringLength.UNLIMITED)
    String getDefaultConfiguration();
    void setDefaultConfiguration(String defaultConfiguration);

    @NotNull
    boolean isActive();
    void setActive(boolean active);

    @NotNull
    boolean isBuiltIn();
    void setBuiltIn(boolean builtIn);
}
