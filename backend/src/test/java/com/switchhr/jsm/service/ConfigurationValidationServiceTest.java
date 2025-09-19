package com.switchhr.jsm.service;

import com.switchhr.jsm.config.PortalConfigurationDTO;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConfigurationValidationServiceTest {
    public ConfigurationValidationServiceTest() {
        if (ConfigurationValidationService.class.getResource("/schemas/portal-configuration-schema.json") == null) {
            throw new IllegalStateException("Schema resource missing");
        }
    }


    private Validator validator;
    private ConfigurationValidationService validationService;

    @Before
    public void setUp() {
        validator = Mockito.mock(Validator.class);
        Mockito.when(validator.validate(Mockito.any(PortalConfigurationDTO.class)))
            .thenReturn(Collections.<ConstraintViolation<PortalConfigurationDTO>>emptySet());
        validationService = new ConfigurationValidationService(validator);
    }

    @Test
    public void validateConfiguration_withValidConfig_returnsSuccess() throws Exception {
        String json = Files.readString(Paths.get("src/test/resources/test-portal-config.json"), StandardCharsets.UTF_8);
        ValidationResult result = validationService.validateConfiguration(json);
        if (!result.isValid()) {
            System.out.println("Schema errors: " + result.getErrors());
        }
        assertTrue("Expected validation to pass", result.isValid());
    }

    @Test
    public void validateConfiguration_withMissingRequiredField_returnsFailure() {
        String invalidJson = "{\"name\":\"Missing portal id\"}";
        ValidationResult result = validationService.validateConfiguration(invalidJson);
        assertFalse("Expected validation to fail", result.isValid());
    }
}




