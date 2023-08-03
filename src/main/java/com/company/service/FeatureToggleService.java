package com.company.service;


import com.company.context.FeatureContextImpl;
import com.company.toggles.TogglesNames;

public interface FeatureToggleService {
    boolean isFeatureToggleActive(TogglesNames toggleName, FeatureContextImpl context);
}
