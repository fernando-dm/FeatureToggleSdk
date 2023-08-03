package com.company.context;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class FeatureContextImplShould {

    String tenantId = "tenantId";
    String companyId = "companyId";

    @Test
    @DisplayName("Create context with tenantId and companyId ")
    void createFeatureContext_WithAllProperties() {

        // When
        FeatureContextImpl featureContext = new FeatureContextImpl(tenantId, companyId);

        // Then
        assertEquals(tenantId, featureContext.getTenantId());
        assertEquals(companyId, featureContext.getCompanyId());
    }

    @Test
    @DisplayName("Create context with tenantId")
    void createFeatureContext_WithoutCompanyId() {

        // When
        FeatureContextImpl featureContext = new FeatureContextImpl(tenantId);

        // Then
        assertEquals(tenantId, featureContext.getTenantId());
        assertNull(featureContext.getCompanyId());
    }
}
