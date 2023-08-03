package com.company.config.toggles;

import com.company.context.FeatureContextImpl;
import com.company.service.FeatureToggleService;
import com.company.service.UnleashFeatureToggleService;
import com.company.toggles.TogglesNamesEnum;
import io.getunleash.Unleash;
import java.lang.reflect.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class FeatureToggleConfigurationTest {

    private FeatureToggleConfiguration featureToggleConfiguration;

    @BeforeEach
    void setUp() {
        // Mock the Unleash class
        Unleash mockUnleash = mock(Unleash.class);
        featureToggleConfiguration = new FeatureToggleConfiguration(mockUnleash);
    }

    @Test
    @DisplayName("featureToggleService with unleash is activated and context not activated")
    void testFeatureToggleService_withUnleash() throws NoSuchFieldException, IllegalAccessException {
        // Given
        String toggleActive = "unleash";
        FeatureContextImpl contextNotCreated = new FeatureContextImpl("tenantNotActivated");

        setPrivateField(featureToggleConfiguration, "toggleActive", toggleActive);

        // When
        FeatureToggleService featureToggleService = featureToggleConfiguration.featureToggleService();

        // Then
        assertFalse(featureToggleService.isFeatureToggleActive(TogglesNamesEnum.TENANT_TOGGLE, contextNotCreated));
        assertTrue(featureToggleService instanceof UnleashFeatureToggleService);
    }

    @Test
    @DisplayName("featureToggleService with fakeUnleash all is activated")
    void testFeatureToggleService_withFakeFeatureFlag() throws NoSuchFieldException, IllegalAccessException {
        // GIVEN
        FeatureContextImpl context = new FeatureContextImpl("tenantNotActivated");
        String toggleActive = "fakeFeatureFlag";
        setPrivateField(featureToggleConfiguration, "toggleActive", toggleActive);

        // WHEN
        FeatureToggleService featureToggleService = featureToggleConfiguration.featureToggleService();

        // Assert
        assertTrue(featureToggleService.isFeatureToggleActive(TogglesNamesEnum.TENANT_TOGGLE, context));
        assertTrue(featureToggleService instanceof UnleashFeatureToggleService);
    }

    @Test
    @DisplayName("featureToggleService with invalid toggle throws exception")
    void testFeatureToggleService_withInvalidConfiguration() throws NoSuchFieldException, IllegalAccessException {
        // GIVEN
        String toggleActive = "invalidToggle";
        setPrivateField(featureToggleConfiguration, "toggleActive", toggleActive);

        // When & Assert
        Throwable exception = assertThrows(IllegalArgumentException.class, featureToggleConfiguration::featureToggleService);
        assertEquals("Invalid feature toggle service configuration", exception.getMessage());
    }

    // Helper method to set private field using reflection
    private void setPrivateField(Object object, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }

}
