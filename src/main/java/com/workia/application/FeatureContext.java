package com.workia.application;

public interface FeatureContext {
    FeatureContextImpl createFeatureContext(String tenantId, String companyId);
    FeatureContextImpl createFeatureContext(String tenantId);

}
