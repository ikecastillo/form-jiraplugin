package com.switchhr.jsm.service;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
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
import javax.validation.constraints.NotNull;
import javax.validation.ValidationException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ConfigurationValidationService {

    private static final String SCHEMA_RESOURCE = "/schemas/portal-configuration-schema.json";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JsonSchema portalConfigSchema;
    private final Validator validator;

    public ConfigurationValidationService(@ComponentImport Validator validator) {
        this.validator = validator;
        this.portalConfigSchema = loadSchema();
    }

    private JsonSchema loadSchema() {
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
        InputStream schemaStream = ConfigurationValidationService.class.getResourceAsStream(SCHEMA_RESOURCE);
        if (schemaStream == null) {
            throw new IllegalStateException("Portal configuration schema not found at " + SCHEMA_RESOURCE);
        }
        return factory.getSchema(schemaStream);
    }

    public ValidationResult validateConfiguration(@NotNull String configurationJson) {
        try {
            JsonNode configNode = objectMapper.readTree(configurationJson);
            Set<ValidationMessage> schemaErrors = portalConfigSchema.validate(configNode);

            if (!schemaErrors.isEmpty()) {
                List<String> messages = schemaErrors.stream()
                    .map(ValidationMessage::getMessage)
                    .collect(Collectors.toList());
                return ValidationResult.failure(messages);
            }

            PortalConfigurationDTO config = objectMapper.readValue(configurationJson, PortalConfigurationDTO.class);
            Set<ConstraintViolation<PortalConfigurationDTO>> violations = validator.validate(config);

            if (!violations.isEmpty()) {
                List<String> messages = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.toList());
                return ValidationResult.failure(messages);
            }

            return validateBusinessRules(config);
        } catch (ValidationException e) {
            return ValidationResult.failure(e.getMessage());
        } catch (Exception e) {
            return ValidationResult.failure("Invalid JSON format: " + e.getMessage());
        }
    }

    private ValidationResult validateBusinessRules(PortalConfigurationDTO config) {
        if (config.getPermissions() != null && Boolean.FALSE.equals(config.getPermissions().getPublicAccess())) {
            List<String> allowed = config.getPermissions().getAllowedGroups();
            if (allowed == null || allowed.isEmpty()) {
                return ValidationResult.failure("Non-public portals must define at least one allowed group");
            }
        }
        return ValidationResult.success();
    }
}

