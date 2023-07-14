package com.workia.application;


public interface FeatureToggleService {
    boolean isFeatureToggleActive(TogglesNames toggleName, FeatureContextImpl context);
}
