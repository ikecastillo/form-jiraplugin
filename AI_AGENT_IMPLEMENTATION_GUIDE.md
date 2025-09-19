# AI Agent Implementation Guide - HR Portal Plugin Enhancement

## Agent Mission Statement

You are an expert Atlassian plugin developer tasked with implementing a comprehensive enhancement to the HR Portal Plugin. Your goal is to transform the existing basic plugin into a scalable, component-based system with advanced Jira integration, servlet filters, and dynamic portal configurations using JSON and ActiveObjects.

## Prerequisites Analysis

Before beginning implementation, thoroughly analyze the existing codebase:

### 1. Codebase Structure Review
```bash
# Examine the current project structure
ls -la /Users/ikecastillo/jiraformplugin/form-jiraplugin/
tree backend/src/main/
tree frontend/
```

### 2. Read and Understand Base Documents
**REQUIRED READING** (in order):
1. `HR_PORTAL_COMPREHENSIVE_ANALYSIS.md` - Complete technical analysis and recommendations
2. `backend/pom.xml` - Current dependencies and Atlassian SDK version
3. `backend/src/main/resources/atlassian-plugin.xml` - Plugin configuration
4. `frontend/package.json` - Frontend dependencies and build scripts
5. `backend/src/main/java/com/switchhr/jsm/servlet/HRPortalServlet.java` - Current implementation

### 3. Key Technologies to Implement
- **ActiveObjects**: Atlassian's ORM for database operations
- **JSON Configuration**: Dynamic portal and component configurations
- **Servlet Filters**: Security and authentication layers
- **Service Management API**: Jira ticket creation and management
- **Component Registry**: Dynamic component loading system

## Phase 1: Database Foundation with ActiveObjects

### Step 1.1: Add ActiveObjects Dependencies
Add to `backend/pom.xml`:
```xml
<dependency>
    <groupId>com.atlassian.activeobjects</groupId>
    <artifactId>activeobjects-plugin</artifactId>
    <version>6.0.0</version>
    <scope>provided</scope>
</dependency>
<dependency>
    <groupId>com.atlassian.activeobjects</groupId>
    <artifactId>activeobjects-spi</artifactId>
    <version>6.0.0</version>
    <scope>provided</scope>
</dependency>
```

### Step 1.2: Update atlassian-plugin.xml
Add ActiveObjects component import:
```xml
<component-import key="ao" interface="com.atlassian.activeobjects.external.ActiveObjects">
    <description>Component to access Active Objects functionality from the plugin</description>
</component-import>

<ao key="ao-module">
    <description>The module configuring the Active Objects service used by this plugin</description>
    <entity>com.switchhr.jsm.model.PortalConfiguration</entity>
    <entity>com.switchhr.jsm.model.ComponentDefinition</entity>
    <entity>com.switchhr.jsm.model.ComponentInstance</entity>
    <entity>com.switchhr.jsm.model.PortalPermission</entity>
    <entity>com.switchhr.jsm.model.AuditRecord</entity>
</ao>
```

### Step 1.3: Create ActiveObjects Entity Models
Create directory structure: `backend/src/main/java/com/switchhr/jsm/model/`

#### PortalConfiguration.java
```java
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
    
    // Relationship methods
    ComponentInstance[] getComponentInstances();
    PortalPermission[] getPermissions();
}
```

#### ComponentDefinition.java
```java
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
```

#### ComponentInstance.java
```java
package com.switchhr.jsm.model;

import net.java.ao.Entity;
import net.java.ao.ManyToOne;
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
    
    @ManyToOne
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
```

#### AuditRecord.java
```java
package com.switchhr.jsm.model;

import net.java.ao.Entity;
import net.java.ao.Preload;
import net.java.ao.schema.Indexed;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.StringLength;
import net.java.ao.schema.Table;

import java.util.Date;

@Table("AUDIT_RECORD")
@Preload
public interface AuditRecord extends Entity {
    
    @NotNull
    @StringLength(100)
    String getUserKey();
    void setUserKey(String userKey);
    
    @NotNull
    @StringLength(255)
    String getUserName();
    void setUserName(String userName);
    
    @StringLength(100)
    String getPortalId();
    void setPortalId(String portalId);
    
    @NotNull
    @StringLength(100)
    String getAction();
    void setAction(String action);
    
    @StringLength(StringLength.UNLIMITED)
    String getDetails();
    void setDetails(String details);
    
    @NotNull
    @Indexed
    Date getTimestamp();
    void setTimestamp(Date timestamp);
    
    @StringLength(50)
    String getIpAddress();
    void setIpAddress(String ipAddress);
    
    @StringLength(500)
    String getUserAgent();
    void setUserAgent(String userAgent);
    
    @StringLength(100)
    String getIssueKey();
    void setIssueKey(String issueKey);
}
```

### Step 1.4: Create Service Layer for ActiveObjects
Create directory: `backend/src/main/java/com/switchhr/jsm/service/`

#### PortalConfigurationService.java
```java
package com.switchhr.jsm.service;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.switchhr.jsm.model.ComponentInstance;
import com.switchhr.jsm.model.PortalConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
@Scanned
public class PortalConfigurationService {
    
    @ComponentImport
    private final ActiveObjects activeObjects;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Autowired
    public PortalConfigurationService(ActiveObjects activeObjects) {
        this.activeObjects = activeObjects;
    }
    
    @Nonnull
    public PortalConfiguration createPortalConfiguration(String portalId, String name, 
                                                        String configurationJson, String userKey) {
        return activeObjects.executeInTransaction(() -> {
            final PortalConfiguration config = activeObjects.create(PortalConfiguration.class);
            config.setPortalId(portalId);
            config.setName(name);
            config.setConfigurationJson(configurationJson);
            config.setActive(true);
            config.setCreatedDate(new Date());
            config.setLastModified(new Date());
            config.setCreatedBy(userKey);
            config.setModifiedBy(userKey);
            config.save();
            return config;
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
        PortalConfiguration[] configs = activeObjects.find(PortalConfiguration.class, 
            "ACTIVE = ?", true);
        return Arrays.asList(configs);
    }
    
    public void updatePortalConfiguration(String portalId, String configurationJson, String userKey) {
        activeObjects.executeInTransaction(() -> {
            PortalConfiguration[] configs = activeObjects.find(PortalConfiguration.class, 
                "PORTAL_ID = ?", portalId);
            
            if (configs.length > 0) {
                PortalConfiguration config = configs[0];
                config.setConfigurationJson(configurationJson);
                config.setLastModified(new Date());
                config.setModifiedBy(userKey);
                config.save();
            }
            return null;
        });
    }
    
    public void deletePortalConfiguration(String portalId) {
        activeObjects.executeInTransaction(() -> {
            PortalConfiguration[] configs = activeObjects.find(PortalConfiguration.class, 
                "PORTAL_ID = ?", portalId);
            
            if (configs.length > 0) {
                PortalConfiguration config = configs[0];
                config.setActive(false);
                config.setLastModified(new Date());
                config.save();
            }
            return null;
        });
    }
}
```

## Phase 2: JSON Configuration System

### Step 2.1: Create JSON Schema Classes
Create directory: `backend/src/main/java/com/switchhr/jsm/config/`

#### PortalConfigurationDTO.java
```java
package com.switchhr.jsm.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

public class PortalConfigurationDTO {
    
    @NotBlank
    @JsonProperty("portalId")
    private String portalId;
    
    @NotBlank
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("description")
    private String description;
    
    @NotNull
    @Valid
    @JsonProperty("branding")
    private BrandingConfig branding;
    
    @NotNull
    @Valid
    @JsonProperty("layout")
    private LayoutConfig layout;
    
    @NotNull
    @Valid
    @JsonProperty("components")
    private List<ComponentInstanceConfig> components;
    
    @NotNull
    @Valid
    @JsonProperty("permissions")
    private PortalPermissionsConfig permissions;
    
    @NotNull
    @Valid
    @JsonProperty("jiraIntegration")
    private JiraIntegrationConfig jiraIntegration;
    
    @JsonProperty("customSettings")
    private Map<String, Object> customSettings;
    
    // Getters and setters
    public String getPortalId() { return portalId; }
    public void setPortalId(String portalId) { this.portalId = portalId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BrandingConfig getBranding() { return branding; }
    public void setBranding(BrandingConfig branding) { this.branding = branding; }
    
    public LayoutConfig getLayout() { return layout; }
    public void setLayout(LayoutConfig layout) { this.layout = layout; }
    
    public List<ComponentInstanceConfig> getComponents() { return components; }
    public void setComponents(List<ComponentInstanceConfig> components) { this.components = components; }
    
    public PortalPermissionsConfig getPermissions() { return permissions; }
    public void setPermissions(PortalPermissionsConfig permissions) { this.permissions = permissions; }
    
    public JiraIntegrationConfig getJiraIntegration() { return jiraIntegration; }
    public void setJiraIntegration(JiraIntegrationConfig jiraIntegration) { this.jiraIntegration = jiraIntegration; }
    
    public Map<String, Object> getCustomSettings() { return customSettings; }
    public void setCustomSettings(Map<String, Object> customSettings) { this.customSettings = customSettings; }
}
```

#### ComponentInstanceConfig.java
```java
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
    private Boolean enabled = true;
    
    @NotNull
    @JsonProperty("sortOrder")
    private Integer sortOrder = 0;
    
    // Getters and setters
    public String getInstanceId() { return instanceId; }
    public void setInstanceId(String instanceId) { this.instanceId = instanceId; }
    
    public String getComponentId() { return componentId; }
    public void setComponentId(String componentId) { this.componentId = componentId; }
    
    public LayoutPosition getPosition() { return position; }
    public void setPosition(LayoutPosition position) { this.position = position; }
    
    public Map<String, Object> getConfiguration() { return configuration; }
    public void setConfiguration(Map<String, Object> configuration) { this.configuration = configuration; }
    
    public List<DisplayCondition> getConditions() { return conditions; }
    public void setConditions(List<DisplayCondition> conditions) { this.conditions = conditions; }
    
    public List<String> getPermissions() { return permissions; }
    public void setPermissions(List<String> permissions) { this.permissions = permissions; }
    
    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
    
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
}
```

#### JiraIntegrationConfig.java
```java
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
    private Boolean enableAutoAssignment = false;
    
    @JsonProperty("assignmentRules")
    private List<AssignmentRule> assignmentRules;
    
    // Getters and setters
    public String getServiceDeskId() { return serviceDeskId; }
    public void setServiceDeskId(String serviceDeskId) { this.serviceDeskId = serviceDeskId; }
    
    public String getDefaultRequestTypeId() { return defaultRequestTypeId; }
    public void setDefaultRequestTypeId(String defaultRequestTypeId) { this.defaultRequestTypeId = defaultRequestTypeId; }
    
    public Map<String, String> getFieldMappings() { return fieldMappings; }
    public void setFieldMappings(Map<String, String> fieldMappings) { this.fieldMappings = fieldMappings; }
    
    public WorkflowConfig getWorkflowConfig() { return workflowConfig; }
    public void setWorkflowConfig(WorkflowConfig workflowConfig) { this.workflowConfig = workflowConfig; }
    
    public NotificationConfig getNotificationConfig() { return notificationConfig; }
    public void setNotificationConfig(NotificationConfig notificationConfig) { this.notificationConfig = notificationConfig; }
    
    public Map<String, Object> getCustomFields() { return customFields; }
    public void setCustomFields(Map<String, Object> customFields) { this.customFields = customFields; }
    
    public Boolean getEnableAutoAssignment() { return enableAutoAssignment; }
    public void setEnableAutoAssignment(Boolean enableAutoAssignment) { this.enableAutoAssignment = enableAutoAssignment; }
    
    public List<AssignmentRule> getAssignmentRules() { return assignmentRules; }
    public void setAssignmentRules(List<AssignmentRule> assignmentRules) { this.assignmentRules = assignmentRules; }
}
```

### Step 2.2: JSON Configuration Validation Service
```java
package com.switchhr.jsm.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import com.switchhr.jsm.config.PortalConfigurationDTO;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.InputStream;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ConfigurationValidationService {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JsonSchema portalConfigSchema;
    private final Validator validator;
    
    public ConfigurationValidationService(Validator validator) {
        this.validator = validator;
        
        // Load JSON schema for portal configuration
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
        InputStream schemaStream = getClass().getResourceAsStream("/schemas/portal-configuration-schema.json");
        this.portalConfigSchema = factory.getSchema(schemaStream);
    }
    
    public ValidationResult validateConfiguration(String configurationJson) {
        try {
            // JSON Schema validation
            JsonNode configNode = objectMapper.readTree(configurationJson);
            Set<ValidationMessage> schemaErrors = portalConfigSchema.validate(configNode);
            
            if (!schemaErrors.isEmpty()) {
                return ValidationResult.failure(schemaErrors.stream()
                    .map(ValidationMessage::getMessage)
                    .collect(Collectors.toList()));
            }
            
            // Java Bean validation
            PortalConfigurationDTO config = objectMapper.readValue(configurationJson, PortalConfigurationDTO.class);
            Set<ConstraintViolation<PortalConfigurationDTO>> violations = validator.validate(config);
            
            if (!violations.isEmpty()) {
                return ValidationResult.failure(violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.toList()));
            }
            
            // Custom business logic validation
            return validateBusinessRules(config);
            
        } catch (Exception e) {
            return ValidationResult.failure("Invalid JSON format: " + e.getMessage());
        }
    }
    
    private ValidationResult validateBusinessRules(PortalConfigurationDTO config) {
        // Implement custom validation logic here
        // - Check component dependencies
        // - Validate permission consistency
        // - Verify Jira integration settings
        return ValidationResult.success();
    }
}
```

## Phase 3: Servlet Filter Implementation

### Step 3.1: Create Filter Package
Create directory: `backend/src/main/java/com/switchhr/jsm/filter/`

#### HRPortalAuthenticationFilter.java
```java
package com.switchhr.jsm.filter;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.sal.api.ApplicationProperties;
import com.switchhr.jsm.service.PortalPermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

public class HRPortalAuthenticationFilter implements Filter {
    
    private static final Logger log = LoggerFactory.getLogger(HRPortalAuthenticationFilter.class);
    
    private PortalPermissionService permissionService;
    private ApplicationProperties applicationProperties;
    private JiraAuthenticationContext authenticationContext;
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.permissionService = ComponentAccessor.getOSGiComponentInstanceOfType(PortalPermissionService.class);
        this.applicationProperties = ComponentAccessor.getOSGiComponentInstanceOfType(ApplicationProperties.class);
        this.authenticationContext = ComponentAccessor.getJiraAuthenticationContext();
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        try {
            // Extract portal ID from request
            String portalId = extractPortalId(httpRequest);
            
            // Check if user is authenticated
            ApplicationUser currentUser = authenticationContext.getLoggedInUser();
            
            if (currentUser == null) {
                log.debug("User not authenticated, redirecting to login");
                redirectToLogin(httpRequest, httpResponse);
                return;
            }
            
            // Check portal-specific permissions
            if (portalId != null && !permissionService.hasPortalAccess(currentUser, portalId)) {
                log.warn("User {} denied access to portal {}", currentUser.getKey(), portalId);
                httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                httpResponse.setContentType("application/json");
                httpResponse.getWriter().write(
                    "{\"error\":\"Access denied\",\"message\":\"You do not have permission to access this portal\"}"
                );
                return;
            }
            
            // Add user context to request for downstream processing
            httpRequest.setAttribute("currentUser", currentUser);
            httpRequest.setAttribute("portalId", portalId);
            
            log.debug("User {} authenticated for portal {}", currentUser.getKey(), portalId);
            
            // Continue with the filter chain
            chain.doFilter(request, response);
            
        } catch (Exception e) {
            log.error("Error in authentication filter", e);
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    private String extractPortalId(HttpServletRequest request) {
        // Extract portal ID from URL path or parameter
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.startsWith("/portal/")) {
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length > 2) {
                return pathParts[2];
            }
        }
        
        // Fallback to request parameter
        return request.getParameter("portalId");
    }
    
    private void redirectToLogin(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String baseUrl = applicationProperties.getBaseUrl();
        String currentUrl = request.getRequestURL().toString();
        
        if (request.getQueryString() != null) {
            currentUrl += "?" + request.getQueryString();
        }
        
        String loginUrl = baseUrl + "/login.jsp?os_destination=" + 
                         URLEncoder.encode(currentUrl, "UTF-8");
        
        response.sendRedirect(loginUrl);
    }
    
    @Override
    public void destroy() {
        // Cleanup resources if needed
    }
}
```

#### CSRFProtectionFilter.java
```java
package com.switchhr.jsm.filter;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

public class CSRFProtectionFilter implements Filter {
    
    private static final Logger log = LoggerFactory.getLogger(CSRFProtectionFilter.class);
    private static final String CSRF_TOKEN_HEADER = "X-CSRF-Token";
    private static final String CSRF_TOKEN_PARAM = "csrf_token";
    private static final String CSRF_TOKEN_SESSION_ATTR = "csrf_token";
    
    private final SecureRandom secureRandom = new SecureRandom();
    private JiraAuthenticationContext authenticationContext;
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.authenticationContext = ComponentAccessor.getJiraAuthenticationContext();
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        try {
            String method = httpRequest.getMethod();
            
            // Generate and provide CSRF token for all requests
            String csrfToken = getOrCreateCSRFToken(httpRequest);
            httpResponse.setHeader(CSRF_TOKEN_HEADER, csrfToken);
            
            // Validate CSRF token for state-changing operations
            if (isStateChangingMethod(method)) {
                if (!validateCSRFToken(httpRequest)) {
                    log.warn("CSRF token validation failed for request from {}", 
                            httpRequest.getRemoteAddr());
                    
                    httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    httpResponse.setContentType("application/json");
                    httpResponse.getWriter().write(
                        "{\"error\":\"CSRF validation failed\",\"message\":\"Invalid or missing CSRF token\"}"
                    );
                    return;
                }
            }
            
            // Continue with the filter chain
            chain.doFilter(request, response);
            
        } catch (Exception e) {
            log.error("Error in CSRF protection filter", e);
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    private String getOrCreateCSRFToken(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String token = (String) session.getAttribute(CSRF_TOKEN_SESSION_ATTR);
        
        if (token == null) {
            token = generateCSRFToken();
            session.setAttribute(CSRF_TOKEN_SESSION_ATTR, token);
        }
        
        return token;
    }
    
    private String generateCSRFToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
    
    private boolean validateCSRFToken(HttpServletRequest request) {
        String sessionToken = getOrCreateCSRFToken(request);
        
        // Check header first
        String providedToken = request.getHeader(CSRF_TOKEN_HEADER);
        
        // Fallback to parameter
        if (providedToken == null) {
            providedToken = request.getParameter(CSRF_TOKEN_PARAM);
        }
        
        return sessionToken != null && sessionToken.equals(providedToken);
    }
    
    private boolean isStateChangingMethod(String method) {
        return "POST".equals(method) || "PUT".equals(method) || 
               "DELETE".equals(method) || "PATCH".equals(method);
    }
    
    @Override
    public void destroy() {
        // Cleanup resources if needed
    }
}
```

### Step 3.2: Update atlassian-plugin.xml with Filters
```xml
<!-- Add after the servlet definition -->
<servlet-filter key="hr-portal-auth-filter" 
                class="com.switchhr.jsm.filter.HRPortalAuthenticationFilter">
    <description>HR Portal Authentication Filter</description>
    <url-pattern>/plugins/servlet/hr-portal/*</url-pattern>
    <dispatcher>REQUEST</dispatcher>
    <dispatcher>FORWARD</dispatcher>
</servlet-filter>

<servlet-filter key="hr-portal-csrf-filter" 
                class="com.switchhr.jsm.filter.CSRFProtectionFilter">
    <description>CSRF Protection for HR Portal API</description>
    <url-pattern>/plugins/servlet/hr-portal/api/*</url-pattern>
    <dispatcher>REQUEST</dispatcher>
    <dispatcher>FORWARD</dispatcher>
</servlet-filter>
```

## Phase 4: Enhanced Servlet with REST API

### Step 4.1: Create REST Controller
Create directory: `backend/src/main/java/com/switchhr/jsm/rest/`

#### HRPortalRestController.java
```java
package com.switchhr.jsm.rest;

import com.atlassian.jira.user.ApplicationUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.switchhr.jsm.config.PortalConfigurationDTO;
import com.switchhr.jsm.service.JiraServiceDeskIntegrationService;
import com.switchhr.jsm.service.PortalConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HRPortalRestController {
    
    private final PortalConfigurationService configurationService;
    private final JiraServiceDeskIntegrationService jiraService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Autowired
    public HRPortalRestController(PortalConfigurationService configurationService,
                                 JiraServiceDeskIntegrationService jiraService) {
        this.configurationService = configurationService;
        this.jiraService = jiraService;
    }
    
    @GET
    @Path("/portals/{portalId}/config")
    public Response getPortalConfiguration(@PathParam("portalId") String portalId,
                                         @Context HttpServletRequest request) {
        try {
            ApplicationUser currentUser = (ApplicationUser) request.getAttribute("currentUser");
            
            return configurationService.getPortalConfiguration(portalId)
                .map(config -> Response.ok(config.getConfigurationJson()).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
                
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Failed to retrieve configuration"))
                .build();
        }
    }
    
    @PUT
    @Path("/portals/{portalId}/config")
    public Response updatePortalConfiguration(@PathParam("portalId") String portalId,
                                            PortalConfigurationDTO configuration,
                                            @Context HttpServletRequest request) {
        try {
            ApplicationUser currentUser = (ApplicationUser) request.getAttribute("currentUser");
            String configJson = objectMapper.writeValueAsString(configuration);
            
            configurationService.updatePortalConfiguration(portalId, configJson, currentUser.getKey());
            
            return Response.ok(Map.of("success", true, "message", "Configuration updated")).build();
            
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", "Failed to update configuration", "details", e.getMessage()))
                .build();
        }
    }
    
    @GET
    @Path("/jira/service-desks/{serviceDeskId}/request-types")
    public Response getRequestTypes(@PathParam("serviceDeskId") String serviceDeskId,
                                  @QueryParam("search") String search) {
        try {
            List<Map<String, Object>> requestTypes = jiraService.getRequestTypes(serviceDeskId, search);
            return Response.ok(requestTypes).build();
            
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Failed to retrieve request types"))
                .build();
        }
    }
    
    @POST
    @Path("/jira/requests")
    public Response createServiceRequest(Map<String, Object> requestData,
                                       @Context HttpServletRequest request) {
        try {
            ApplicationUser currentUser = (ApplicationUser) request.getAttribute("currentUser");
            String portalId = (String) request.getAttribute("portalId");
            
            String issueKey = jiraService.createServiceRequest(requestData, currentUser, portalId);
            
            return Response.status(Response.Status.CREATED)
                .entity(Map.of("success", true, "issueKey", issueKey))
                .build();
                
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", "Failed to create request", "details", e.getMessage()))
                .build();
        }
    }
}
```

### Step 4.2: Update atlassian-plugin.xml for REST
```xml
<!-- Add REST resource -->
<rest key="hr-portal-rest" path="/hr-portal" version="1.0">
    <description>HR Portal REST API</description>
</rest>
```

## Phase 5: Jira Service Management Integration

### Step 5.1: Create Jira Integration Service
#### JiraServiceDeskIntegrationService.java
```java
package com.switchhr.jsm.service;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.sal.api.ApplicationProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Component
public class JiraServiceDeskIntegrationService {
    
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ApplicationProperties applicationProperties;
    
    public JiraServiceDeskIntegrationService() {
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
        this.applicationProperties = ComponentAccessor.getOSGiComponentInstanceOfType(ApplicationProperties.class);
    }
    
    @Cacheable(value = "requestTypes", key = "#serviceDeskId + '_' + #search")
    public List<Map<String, Object>> getRequestTypes(String serviceDeskId, String search) {
        try {
            String endpoint = String.format("/rest/servicedeskapi/servicedesk/%s/requesttype", serviceDeskId);
            
            if (search != null && !search.trim().isEmpty()) {
                endpoint += "?searchQuery=" + java.net.URLEncoder.encode(search, "UTF-8");
            }
            
            HttpResponse<String> response = makeJiraApiRequest(endpoint, "GET", null);
            
            if (response.statusCode() == 200) {
                Map<String, Object> responseData = objectMapper.readValue(response.body(), 
                    new TypeReference<Map<String, Object>>() {});
                return (List<Map<String, Object>>) responseData.get("values");
            } else {
                throw new RuntimeException("Failed to fetch request types: " + response.body());
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Error fetching request types", e);
        }
    }
    
    public String createServiceRequest(Map<String, Object> requestData, 
                                     ApplicationUser user, String portalId) {
        try {
            // Build the request payload
            Map<String, Object> payload = buildServiceRequestPayload(requestData, user, portalId);
            
            HttpResponse<String> response = makeJiraApiRequest(
                "/rest/servicedeskapi/request", 
                "POST", 
                objectMapper.writeValueAsString(payload)
            );
            
            if (response.statusCode() == 201) {
                Map<String, Object> responseData = objectMapper.readValue(response.body(), 
                    new TypeReference<Map<String, Object>>() {});
                return (String) responseData.get("issueKey");
            } else {
                throw new RuntimeException("Failed to create service request: " + response.body());
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Error creating service request", e);
        }
    }
    
    public CompletableFuture<List<Map<String, Object>>> getRequestTypeFieldsAsync(String serviceDeskId, 
                                                                                 String requestTypeId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String endpoint = String.format(
                    "/rest/servicedeskapi/servicedesk/%s/requesttype/%s/field", 
                    serviceDeskId, requestTypeId
                );
                
                HttpResponse<String> response = makeJiraApiRequest(endpoint, "GET", null);
                
                if (response.statusCode() == 200) {
                    Map<String, Object> responseData = objectMapper.readValue(response.body(), 
                        new TypeReference<Map<String, Object>>() {});
                    return (List<Map<String, Object>>) responseData.get("requestTypeFields");
                } else {
                    throw new RuntimeException("Failed to fetch request type fields: " + response.body());
                }
                
            } catch (Exception e) {
                throw new RuntimeException("Error fetching request type fields", e);
            }
        });
    }
    
    private Map<String, Object> buildServiceRequestPayload(Map<String, Object> requestData, 
                                                          ApplicationUser user, String portalId) {
        Map<String, Object> payload = new HashMap<>();
        
        payload.put("serviceDeskId", requestData.get("serviceDeskId"));
        payload.put("requestTypeId", requestData.get("requestTypeId"));
        
        // Map form fields to Jira fields
        Map<String, Object> fieldValues = new HashMap<>();
        Map<String, Object> formData = (Map<String, Object>) requestData.get("formData");
        
        if (formData != null) {
            // Map standard fields
            if (formData.containsKey("summary")) {
                fieldValues.put("summary", formData.get("summary"));
            }
            if (formData.containsKey("description")) {
                fieldValues.put("description", formData.get("description"));
            }
            
            // Map custom fields based on portal configuration
            Map<String, String> fieldMappings = getFieldMappingsForPortal(portalId);
            for (Map.Entry<String, String> mapping : fieldMappings.entrySet()) {
                String formFieldName = mapping.getKey();
                String jiraFieldId = mapping.getValue();
                
                if (formData.containsKey(formFieldName)) {
                    Object value = formData.get(formFieldName);
                    fieldValues.put(jiraFieldId, formatFieldValue(value, jiraFieldId));
                }
            }
        }
        
        payload.put("requestFieldValues", fieldValues);
        
        // Add request participants if specified
        if (requestData.containsKey("requestParticipants")) {
            payload.put("requestParticipants", requestData.get("requestParticipants"));
        }
        
        return payload;
    }
    
    private Object formatFieldValue(Object value, String jiraFieldId) {
        // Handle different field types
        if (value instanceof String) {
            String strValue = (String) value;
            
            // For select fields, wrap in value object
            if (jiraFieldId.startsWith("customfield_") && !strValue.isEmpty()) {
                return Map.of("value", strValue);
            }
        } else if (value instanceof List) {
            // For multi-select fields
            List<?> listValue = (List<?>) value;
            return listValue.stream()
                .map(item -> Map.of("value", item.toString()))
                .toArray();
        }
        
        return value;
    }
    
    private Map<String, String> getFieldMappingsForPortal(String portalId) {
        // This should be loaded from portal configuration
        // For now, return default mappings
        Map<String, String> mappings = new HashMap<>();
        mappings.put("employeeName", "customfield_10001");
        mappings.put("effectiveDate", "customfield_10002");
        mappings.put("department", "customfield_10003");
        mappings.put("jobTitle", "customfield_10004");
        mappings.put("manager", "customfield_10005");
        return mappings;
    }
    
    private HttpResponse<String> makeJiraApiRequest(String endpoint, String method, String body) 
            throws IOException, InterruptedException {
        
        String baseUrl = applicationProperties.getBaseUrl();
        URI uri = URI.create(baseUrl + endpoint);
        
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
            .uri(uri)
            .header("Accept", "application/json")
            .header("X-Atlassian-Token", "nocheck")
            .timeout(Duration.ofSeconds(30));
        
        // Add authentication header
        // Note: In a plugin context, the request inherits the user's session
        // For external API calls, you would need to use service account credentials
        
        if ("POST".equals(method) || "PUT".equals(method)) {
            requestBuilder.header("Content-Type", "application/json");
            requestBuilder.method(method, HttpRequest.BodyPublishers.ofString(body != null ? body : ""));
        } else {
            requestBuilder.method(method, HttpRequest.BodyPublishers.noBody());
        }
        
        return httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
    }
}
```

## Phase 6: Frontend Enhancements

### Step 6.1: Update package.json with Additional Dependencies
Add to `frontend/package.json`:
```json
{
  "dependencies": {
    "@tanstack/react-query": "^5.0.0",
    "react-hook-form": "^7.48.0",
    "@hookform/resolvers": "^3.3.0",
    "zod": "^3.22.0",
    "react-dropzone": "^14.2.0",
    "date-fns": "^2.30.0"
  }
}
```

### Step 6.2: Create API Service Layer
Create file: `frontend/lib/api.ts`
```typescript
class HRPortalAPI {
  private baseURL: string;
  
  constructor() {
    this.baseURL = '/plugins/servlet/hr-portal/api';
  }
  
  private async makeRequest<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
    const response = await fetch(`${this.baseURL}${endpoint}`, {
      ...options,
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
        'X-Atlassian-Token': 'nocheck',
        ...options.headers
      },
      credentials: 'same-origin'
    });
    
    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      throw new Error(errorData.message || `HTTP ${response.status}: ${response.statusText}`);
    }
    
    return response.json();
  }
  
  async getPortalConfiguration(portalId: string): Promise<PortalConfiguration> {
    return this.makeRequest(`/portals/${portalId}/config`);
  }
  
  async updatePortalConfiguration(portalId: string, config: PortalConfiguration): Promise<void> {
    return this.makeRequest(`/portals/${portalId}/config`, {
      method: 'PUT',
      body: JSON.stringify(config)
    });
  }
  
  async getRequestTypes(serviceDeskId: string, search?: string): Promise<RequestType[]> {
    const params = new URLSearchParams();
    if (search) {
      params.append('search', search);
    }
    
    return this.makeRequest(`/jira/service-desks/${serviceDeskId}/request-types?${params}`);
  }
  
  async createServiceRequest(requestData: ServiceRequestData): Promise<{ issueKey: string }> {
    return this.makeRequest('/jira/requests', {
      method: 'POST',
      body: JSON.stringify(requestData)
    });
  }
  
  async getRequestTypeFields(serviceDeskId: string, requestTypeId: string): Promise<RequestTypeField[]> {
    return this.makeRequest(`/jira/service-desks/${serviceDeskId}/request-types/${requestTypeId}/fields`);
  }
}

export const hrPortalAPI = new HRPortalAPI();
```

### Step 6.3: Create Component Registry System
Create file: `frontend/lib/component-registry.ts`
```typescript
interface ComponentDefinition {
  id: string;
  name: string;
  type: ComponentType;
  component: React.ComponentType<any>;
  defaultConfig: Record<string, any>;
  configSchema: any; // JSON Schema for configuration
}

class ComponentRegistry {
  private static instance: ComponentRegistry;
  private components = new Map<string, ComponentDefinition>();
  
  static getInstance(): ComponentRegistry {
    if (!ComponentRegistry.instance) {
      ComponentRegistry.instance = new ComponentRegistry();
    }
    return ComponentRegistry.instance;
  }
  
  registerComponent(definition: ComponentDefinition): void {
    this.components.set(definition.id, definition);
  }
  
  getComponent(id: string): ComponentDefinition | undefined {
    return this.components.get(id);
  }
  
  getAllComponents(): ComponentDefinition[] {
    return Array.from(this.components.values());
  }
  
  getComponentsByType(type: ComponentType): ComponentDefinition[] {
    return Array.from(this.components.values())
      .filter(comp => comp.type === type);
  }
  
  initializeDefaultComponents(): void {
    // Register existing components
    this.registerComponent({
      id: 'form-selector',
      name: 'Form Selector',
      type: 'form',
      component: React.lazy(() => import('../components/form-selector')),
      defaultConfig: {
        title: 'Select a Form',
        description: 'Choose the appropriate form for your request'
      },
      configSchema: {
        type: 'object',
        properties: {
          title: { type: 'string' },
          description: { type: 'string' },
          showIcons: { type: 'boolean', default: true }
        }
      }
    });
    
    this.registerComponent({
      id: 'transfer-promotion-form',
      name: 'Transfer & Promotion Form',
      type: 'form',
      component: React.lazy(() => import('../app/transfer-promotion/page')),
      defaultConfig: {
        enableValidation: true,
        showProgressIndicator: true
      },
      configSchema: {
        type: 'object',
        properties: {
          enableValidation: { type: 'boolean', default: true },
          showProgressIndicator: { type: 'boolean', default: true }
        }
      }
    });
    
    // Register new dynamic components
    this.registerComponent({
      id: 'link-group',
      name: 'Link Group',
      type: 'content',
      component: React.lazy(() => import('../components/link-group')),
      defaultConfig: {
        title: 'Quick Links',
        links: []
      },
      configSchema: {
        type: 'object',
        properties: {
          title: { type: 'string' },
          links: {
            type: 'array',
            items: {
              type: 'object',
              properties: {
                label: { type: 'string' },
                url: { type: 'string' },
                target: { type: 'string', enum: ['_self', '_blank'] }
              }
            }
          }
        }
      }
    });
  }
}

export const componentRegistry = ComponentRegistry.getInstance();
```

## Phase 7: Testing and Validation

### Step 7.1: Create Test Configuration
Create file: `backend/src/test/resources/test-portal-config.json`
```json
{
  "portalId": "hr-test-portal",
  "name": "HR Test Portal",
  "description": "Test configuration for HR Portal",
  "branding": {
    "logoUrl": "/images/hr-logo.png",
    "primaryColor": "#d31820",
    "customCSS": ""
  },
  "layout": {
    "type": "grid",
    "columns": 12,
    "gap": "1rem",
    "responsive": {
      "sm": { "columns": 4 },
      "md": { "columns": 8 },
      "lg": { "columns": 12 }
    }
  },
  "components": [
    {
      "instanceId": "header-1",
      "componentId": "header",
      "position": { "row": 0, "column": 0, "span": 12 },
      "configuration": {
        "title": "HR Portal",
        "showLogo": true
      },
      "enabled": true,
      "sortOrder": 0
    },
    {
      "instanceId": "form-selector-1",
      "componentId": "form-selector",
      "position": { "row": 1, "column": 0, "span": 12 },
      "configuration": {
        "title": "Select a Form",
        "description": "Choose the appropriate HR form for your request"
      },
      "enabled": true,
      "sortOrder": 1
    }
  ],
  "permissions": {
    "allowedGroups": ["jira-users", "hr-team"],
    "adminGroups": ["hr-admins"],
    "publicAccess": false
  },
  "jiraIntegration": {
    "serviceDeskId": "HR_DESK",
    "defaultRequestTypeId": "GENERAL_REQUEST",
    "fieldMappings": {
      "employeeName": "customfield_10001",
      "effectiveDate": "customfield_10002",
      "department": "customfield_10003"
    },
    "workflowConfig": {
      "enableAutoAssignment": true,
      "defaultAssignee": "hr-team"
    },
    "enableAutoAssignment": true,
    "assignmentRules": [
      {
        "condition": { "department": "IT" },
        "assignee": "it-support"
      }
    ]
  }
}
```

### Step 7.2: Create Integration Tests
Create file: `backend/src/test/java/com/switchhr/jsm/integration/HRPortalIntegrationTest.java`
```java
package com.switchhr.jsm.integration;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.switchhr.jsm.model.PortalConfiguration;
import com.switchhr.jsm.service.PortalConfigurationService;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

@RunWith(ActiveObjectsJUnitRunner.class)
public class HRPortalIntegrationTest {
    
    private EntityManager entityManager;
    private TestActiveObjects activeObjects;
    private PortalConfigurationService configurationService;
    private ObjectMapper objectMapper = new ObjectMapper();
    
    @Before
    public void setUp() {
        assertNotNull(entityManager);
        activeObjects = new TestActiveObjects(entityManager);
        configurationService = new PortalConfigurationService(activeObjects);
    }
    
    @Test
    public void testCreateAndRetrievePortalConfiguration() throws Exception {
        // Load test configuration
        String configJson = new String(Files.readAllBytes(
            Paths.get("src/test/resources/test-portal-config.json")));
        
        // Create portal configuration
        PortalConfiguration config = configurationService.createPortalConfiguration(
            "hr-test-portal", 
            "HR Test Portal", 
            configJson, 
            "test-user"
        );
        
        assertNotNull(config);
        assertEquals("hr-test-portal", config.getPortalId());
        assertEquals("HR Test Portal", config.getName());
        assertTrue(config.isActive());
        
        // Retrieve configuration
        Optional<PortalConfiguration> retrieved = configurationService
            .getPortalConfiguration("hr-test-portal");
        
        assertTrue(retrieved.isPresent());
        assertEquals(configJson, retrieved.get().getConfigurationJson());
    }
    
    @Test
    public void testPortalConfigurationValidation() throws Exception {
        String invalidConfigJson = "{ \"invalid\": \"config\" }";
        
        // This should throw a validation exception
        assertThrows(ValidationException.class, () -> {
            configurationService.createPortalConfiguration(
                "invalid-portal", 
                "Invalid Portal", 
                invalidConfigJson, 
                "test-user"
            );
        });
    }
}
```

## Execution Checklist

### Phase 1 Checklist:
- [ ] Add ActiveObjects dependencies to pom.xml
- [ ] Update atlassian-plugin.xml with AO configuration
- [ ] Create all ActiveObjects entity interfaces
- [ ] Create PortalConfigurationService
- [ ] Test database schema creation

### Phase 2 Checklist:
- [ ] Create JSON configuration DTOs
- [ ] Implement configuration validation service
- [ ] Create JSON schema files in resources/schemas/
- [ ] Test JSON serialization/deserialization

### Phase 3 Checklist:
- [ ] Create authentication filter
- [ ] Create CSRF protection filter
- [ ] Update atlassian-plugin.xml with filter definitions
- [ ] Test filter chain execution

### Phase 4 Checklist:
- [ ] Create REST controller
- [ ] Add REST configuration to atlassian-plugin.xml
- [ ] Implement all REST endpoints
- [ ] Test API endpoints

### Phase 5 Checklist:
- [ ] Create Jira integration service
- [ ] Implement request type fetching
- [ ] Implement service request creation
- [ ] Test Jira API connectivity

### Phase 6 Checklist:
- [ ] Update frontend dependencies
- [ ] Create API service layer
- [ ] Implement component registry
- [ ] Update existing components for dynamic loading

### Phase 7 Checklist:
- [ ] Create test configurations
- [ ] Write integration tests
- [ ] Test full portal workflow
- [ ] Performance testing

## Success Criteria

### Functional Requirements:
1. **Dynamic Portal Configuration**: Portals can be configured via JSON without code changes
2. **Component System**: New components can be registered and used dynamically
3. **Servlet Filters**: Authentication and CSRF protection working with REQUEST/FORWARD dispatchers
4. **Jira Integration**: Request types fetched dynamically, tickets created successfully
5. **Security**: Role-based access control and audit logging implemented
6. **Performance**: Caching implemented, load times optimized

### Technical Requirements:
1. **ActiveObjects**: All data persistence uses AO with proper relationships
2. **JSON Configuration**: Schema validation and error handling implemented
3. **REST API**: Complete CRUD operations for portal management
4. **Error Handling**: Comprehensive error handling with appropriate HTTP status codes
5. **Testing**: Integration tests covering all major workflows
6. **Documentation**: Code documentation and API documentation complete

## Troubleshooting Guide

### Common Issues:
1. **ActiveObjects Migration Failures**: Check entity annotations and database permissions
2. **Filter Chain Issues**: Verify filter order and dispatcher configuration
3. **JSON Validation Errors**: Check schema files and validation service implementation
4. **Jira API Authentication**: Verify plugin permissions and API token configuration
5. **Component Loading Errors**: Check component registry initialization and imports

### Debug Commands:
```bash
# Build and install plugin
mvn clean install

# Check plugin status
http://localhost:2990/jira/plugins/servlet/upm

# View logs
tail -f amps-standalone/target/jira/home/log/atlassian-jira.log

# Test REST endpoints
curl -X GET "http://localhost:2990/jira/plugins/servlet/hr-portal/api/portals/test/config" \
  -H "Content-Type: application/json" \
  --cookie-jar cookies.txt
```

## Final Notes

This implementation guide provides a complete transformation of your HR Portal Plugin into a scalable, component-based system with advanced Jira integration. The use of ActiveObjects ensures proper data persistence within the Atlassian ecosystem, while JSON configuration enables dynamic portal customization without code changes.

Key benefits of this approach:
- **Scalability**: Component-based architecture allows unlimited expansion
- **Flexibility**: JSON configuration enables runtime customization
- **Security**: Comprehensive authentication and authorization system
- **Performance**: Optimized with caching and lazy loading
- **Maintainability**: Clean separation of concerns and modern architecture patterns

Execute the phases sequentially, validating each phase before proceeding to the next. The comprehensive test suite will ensure reliability and catch issues early in development.