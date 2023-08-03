package com.company.context;

public interface FeatureContext {
    FeatureContextImpl createFeatureContext(String tenantId, String companyId);
    FeatureContextImpl createFeatureContext(String tenantId);

}
