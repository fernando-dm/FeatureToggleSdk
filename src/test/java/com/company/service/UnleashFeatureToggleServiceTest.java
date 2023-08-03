package com.company.service;

import com.company.config.unleash.FakeUnleash;
import com.company.context.FeatureContextImpl;
import com.company.toggles.TogglesNames;
import com.company.toggles.TogglesNamesEnum;
import io.getunleash.Constraint;
import io.getunleash.EvaluatedToggle;
import io.getunleash.Operator;
import io.getunleash.UnleashContext;
import io.getunleash.strategy.Strategy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UnleashFeatureToggleServiceTest {
    private final boolean isLoggerActive = true;

    private final String logLevel = "INFO";

    private final String randomToggle1 = "randomToggleName1";
    private final String randomToggle2 = "randomToggleName2";
    private final TogglesNames toggleName = TogglesNamesEnum.TENANT_COMPANY_TOGGLE;
    private String appName = "movies";
    private final FeatureContextImpl tenantIdContext = new FeatureContextImpl(appName, "tenantId");
    private final FeatureContextImpl tenantCompanyContext = new FeatureContextImpl("tenantId", "companyId");

    FakeUnleash fakeUnleash;
    private UnleashFeatureToggleService featureToggleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        fakeUnleash = new FakeUnleash();
        featureToggleService = new UnleashFeatureToggleService(fakeUnleash);

    }

    @Test
    void should_enable_specific_toggles() {
        fakeUnleash.enable(randomToggle1, randomToggle2);

        assertTrue(fakeUnleash.isEnabled(randomToggle1));
        assertTrue(fakeUnleash.isEnabled(randomToggle2));

        assertFalse(fakeUnleash.isEnabled(toggleName.getToggleName()));

    }

    @Test
    void should_disable_all_toggles() {
        fakeUnleash.enable(randomToggle1, randomToggle2);
        fakeUnleash.disableAll();

        assertFalse(fakeUnleash.isEnabled(randomToggle1));
    }

    @Test
    void should_evaluate_all_toggles_without_context() {
        String tenantCompanyToggleToggleName = toggleName.getToggleName();

        // Given toggles enabled
        fakeUnleash.enable("t1", "t2", tenantCompanyToggleToggleName);

        List<EvaluatedToggle> toggles = getToggles();

        // When feature is enabled
        boolean tenantToggleIsEnabled = featureToggleService.isFeatureToggleActive(toggleName, tenantIdContext);
        EvaluatedToggle tenant1 = toggles.get(0);

        // Then
        assertEquals(3, toggles.size());
        assertTrue(tenantToggleIsEnabled);
        assertEquals(tenant1.getName(), tenantCompanyToggleToggleName);
        assertTrue(tenant1.isEnabled());
    }


    @Test
    void should_be_active_when_toggle_is_enabled_without_evaluate_context() {

        fakeUnleash.enable(toggleName.getToggleName());

        List<EvaluatedToggle> toggles =
                fakeUnleash.more().evaluateAllToggles(
                        new UnleashContext.Builder()
                                .addProperty("tenantId", tenantCompanyContext.getTenantId())
                                .build());

        EvaluatedToggle t1 = toggles.get(0);
        assertEquals(toggleName.getToggleName(), t1.getName());
        assertTrue(t1.isEnabled());
    }

    @Test
    void should_be_active_when_toggle_is_enabled_evaluating_context() {

        fakeUnleash.enable(toggleName.getToggleName());

        // Given
        FeatureContextImpl featureContext = new FeatureContextImpl(
                "tenantId", "companyI"
        );

        // When
        boolean isActive = featureToggleService.isFeatureToggleActive(toggleName, featureContext);

        // Then
        assertTrue(isActive);
    }

    @Test
    void should_be_active_when_toggle_is_enabled_without_evaluate_properties() {

        fakeUnleash.enable(toggleName.getToggleName());

        // Given
        Map<String, String> properties = new HashMap<>();
        properties.put("tenantId", "1_apsa");
        properties.put("companyId", "1_sucursal_devoto");


        // When
        boolean isActive = featureToggleService.isFeatureToggleActive(toggleName, properties);

        // Then
        assertTrue(isActive);
    }

    @Test
    void should_be_enabled_when_all_constraints_are_satisfied() {
        List<Constraint> constraints = new ArrayList<>();
        Map<String, String> properties = new HashMap<String, String>();

        // ACTIVO properties
        properties.put("tenantId", "1_oca");
        properties.put("companyId", "1_sucursal_devoto");
        Strategy fakeFeatureTogglesEnabled = new FakeFeatureTogglesEnabled(properties);

        //GIVEN
        UnleashContext context =
                UnleashContext.builder()
                        .environment("test")
                        .addProperty("tenantId", "1_oca")
                        .addProperty("companyId", "1_sucursal_devoto")
                        .build();


        //WHEN

        //operator IN
        constraints.add(new Constraint("tenantId", Operator.IN, Arrays.asList("1_oca")));
        constraints.add(new Constraint("companyId", Operator.IN, Arrays.asList("1_sucursal_devoto", "2_belgrano", "3_saavedra")));

        // evaluate constraints with context
        boolean result = fakeFeatureTogglesEnabled.isEnabled(properties, context, constraints);

        //THEN
        assertTrue(result);
    }


    private class FakeFeatureTogglesEnabled implements Strategy {

        private final Map<String, String> params;

        public FakeFeatureTogglesEnabled(Map<String, String> params) {
            this.params = params;
        }

        @Override
        public String getName() {
            return "enabled";
        }

        @Override
        public boolean isEnabled(Map<String, String> parameters) {
            System.out.println(parameters);
            boolean result = parameters.entrySet().stream()
                    .allMatch(entry -> params.containsKey(entry.getKey())) &&
                    parameters.values().stream()
                            .allMatch(entry -> params.containsValue(entry));
            return result;
        }

        boolean isEnabled(String toggleName, UnleashContext context) {
            return true;
        }

    }

    private List<EvaluatedToggle> getToggles() {
        return fakeUnleash.more().evaluateAllToggles(new UnleashContext.Builder().build());
    }
}