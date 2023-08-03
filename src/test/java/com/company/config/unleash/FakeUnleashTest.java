package com.company.config.unleash;

import io.getunleash.UnleashContext;
import io.getunleash.Variant;
import java.util.function.BiPredicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FakeUnleashTest {

    private FakeUnleash fakeUnleash;

    @BeforeEach
    void setUp() {
        fakeUnleash = new FakeUnleash();
    }

    @Test
    void isEnabled_WithDefaultSetting_Enabled() {
        assertTrue(fakeUnleash.isEnabled("feature_toggle", true));
    }

    @Test
    void isEnabled_WithDefaultSetting_Disabled() {
        assertFalse(fakeUnleash.isEnabled("feature_toggle", false));
    }

    @Test
    void getVariant_WithDefaultVariant_ReturnsDisabledVariant() {
        Variant defaultVariant = fakeUnleash.getVariant("feature_toggle");
        assertEquals(Variant.DISABLED_VARIANT, defaultVariant);
    }

    @Test
    void getVariant_WithCustomVariant_ReturnsCustomVariant() {
        Variant customVariant = new Variant("variant_name", "variant_payload", true);
        fakeUnleash.setVariant("feature_toggle", customVariant);
        fakeUnleash.enable("feature_toggle");

        Variant result = fakeUnleash.getVariant("feature_toggle");
        assertEquals(customVariant, result);
    }

    @Test
    void getFeatureToggleNames_ReturnsEmptyListWhenNoFeatures() {
        assertTrue(fakeUnleash.getFeatureToggleNames().isEmpty());
    }

    @Test
    void getFeatureToggleNames_ReturnsListOfFeatureNames() {
        fakeUnleash.enable("feature_toggle1", "feature_toggle2");
        fakeUnleash.disable("feature_toggle3");

        assertEquals(3, fakeUnleash.getFeatureToggleNames().size());
        assertTrue(fakeUnleash.getFeatureToggleNames().contains("feature_toggle1"));
        assertTrue(fakeUnleash.getFeatureToggleNames().contains("feature_toggle2"));
        assertTrue(fakeUnleash.getFeatureToggleNames().contains("feature_toggle3"));
    }

    @Test
    void more_EvaluateAllToggles() {
        fakeUnleash.enable("feature_toggle1", "feature_toggle2");
        fakeUnleash.disable("feature_toggle3");

        fakeUnleash.setVariant("feature_toggle1", new Variant("variant1", "variant_payload1", true));
        fakeUnleash.setVariant("feature_toggle2", new Variant("variant2", "variant_payload2", true));

        assertEquals(3, fakeUnleash.more().evaluateAllToggles().size());
    }

    @Test
    void more_GetFeatureToggleDefinition() {
        fakeUnleash.enable("feature_toggle1", "feature_toggle2");

        assertNotNull(fakeUnleash.more().getFeatureToggleDefinition("feature_toggle1").orElse(null));
        assertNull(fakeUnleash.more().getFeatureToggleDefinition("feature_toggle3").orElse(null));
    }

    @Test
    void isEnabled_WithUnleashContext_Enabled() {
        String toggleName = "feature_toggle";
        UnleashContext context = mock(UnleashContext.class);

        assertTrue(fakeUnleash.isEnabled(toggleName, context));
    }

    @Test
    void isEnabled_WithContextAndFallbackAction_Enabled() {
        String toggleName = "feature_toggle";
        UnleashContext context = mock(UnleashContext.class);

        fakeUnleash.enable(toggleName);
        BiPredicate fallbackAction = mock(BiPredicate.class);

        assertTrue(fakeUnleash.isEnabled(toggleName, context, fallbackAction));
    }

    @Test
    void isEnabled_WithFallbackAction_FallbackActionExecuted() {
        String toggleName = "feature_toggle";
        BiPredicate<String, UnleashContext> fallbackAction = mock(BiPredicate.class);
        UnleashContext context = UnleashContext.builder().userId("user123").build();

        when(fallbackAction.test(anyString(),
                ArgumentMatchers.any(UnleashContext.class)))
                .thenReturn(false);

        assertFalse(fakeUnleash.isEnabled(toggleName, fallbackAction));
    }


    @Test
    void isEnabled_WithFallbackAction_Enabled() {
        String toggleName = "feature_toggle";
        fakeUnleash.enable(toggleName);
        BiPredicate fallbackAction = mock(BiPredicate.class);

        assertTrue(fakeUnleash.isEnabled(toggleName, fallbackAction));
    }

    @Test
    void getVariant_WithUnleashContext_ReturnsVariant() {
        String toggleName = "feature_toggle";
        UnleashContext context = mock(UnleashContext.class);

        Variant variant = fakeUnleash.getVariant(toggleName, context);
        assertNotNull(variant);
    }

    @Test
    void getVariant_WithDefaultVariant_ReturnsDefaultVariant() {
        String toggleName = "feature_toggle";
        UnleashContext context = mock(UnleashContext.class);
        Variant defaultValue = new Variant("default_variant", "default_payload", true);

        Variant result = fakeUnleash.getVariant(toggleName, context, defaultValue);
        assertEquals(defaultValue, result);
    }

    @Test
    void enableAll_EnablesAllFeatures() {
        fakeUnleash.enable("feature_toggle1", "feature_toggle2");
        fakeUnleash.disable("feature_toggle3");

        fakeUnleash.enableAll();

        assertTrue(fakeUnleash.isEnabled("feature_toggle1", true));
        assertTrue(fakeUnleash.isEnabled("feature_toggle2", true));
        assertTrue(fakeUnleash.isEnabled("feature_toggle3", true));
    }

    @Test
    void disableAll_DisablesAllFeatures() {
        fakeUnleash.enable("feature_toggle1", "feature_toggle2");
        fakeUnleash.disable("feature_toggle3");

        fakeUnleash.disableAll();

        assertFalse(fakeUnleash.isEnabled("feature_toggle1", false));
        assertFalse(fakeUnleash.isEnabled("feature_toggle2", false));
        assertFalse(fakeUnleash.isEnabled("feature_toggle3", false));
    }

}
