package com.workia.application;

public enum TogglesNamesEnum implements TogglesNames {
    TENANT_COMPANY_TOGGLE("tenantCompanyToggle"),
    TENANT_TOGGLE("tenantToggle");

    private final String toggleName;


    TogglesNamesEnum(String toggleName) {
        this.toggleName = toggleName;
    }

    @Override
    public String getToggleName() {
        return toggleName;
    }
}

