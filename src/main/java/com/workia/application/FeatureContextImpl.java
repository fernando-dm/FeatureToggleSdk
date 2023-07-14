package com.workia.application;

public class FeatureContextImpl implements FeatureContext {

    private final String tenantId;
    private String companyId;

    public FeatureContextImpl(String tenantId, String companyId) {
        this.tenantId = tenantId;
        this.companyId = companyId;
    }

    public FeatureContextImpl(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getCompanyId() {
        return companyId;
    }

    @Override
    public FeatureContextImpl createFeatureContext(String tenantId, String companyId) {
        return new FeatureContextImpl(tenantId, companyId);
    }

    @Override
    public FeatureContextImpl createFeatureContext(String tenantId) {
        return new FeatureContextImpl(tenantId);
    }
}
