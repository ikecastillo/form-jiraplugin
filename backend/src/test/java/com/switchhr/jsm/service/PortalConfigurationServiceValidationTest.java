package com.switchhr.jsm.service;

import com.atlassian.activeobjects.external.ActiveObjects;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import javax.validation.ValidationException;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class PortalConfigurationServiceValidationTest {

    private ActiveObjects activeObjects;
    private ConfigurationValidationService validationService;
    private PortalConfigurationService portalConfigurationService;

    @Before
    public void setUp() {
        activeObjects = Mockito.mock(ActiveObjects.class);
        validationService = Mockito.mock(ConfigurationValidationService.class);
        portalConfigurationService = new PortalConfigurationService(activeObjects, validationService);
    }

    @Test(expected = ValidationException.class)
    public void createPortalConfiguration_rejectsInvalidConfig() {
        Mockito.when(validationService.validateConfiguration("{}"))
            .thenReturn(ValidationResult.failure("portalId is required"));

        try {
            portalConfigurationService.createPortalConfiguration("portal", "Portal", "{}", "user");
        } finally {
            verify(activeObjects, never()).executeInTransaction(ArgumentMatchers.any());
        }
    }
}
