package com.workia.application;

import io.getunleash.Unleash;
import io.getunleash.UnleashContext;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UnleashFeatureToggleService implements FeatureToggleService {
    private final Unleash unleash;

    public UnleashFeatureToggleService(Unleash unleash) {
        this.unleash = unleash;
    }

    @Override
    public boolean isFeatureToggleActive(TogglesNames toggleName, FeatureContextImpl featureContextImpl) {
        UnleashContext.Builder contextBuilder = UnleashContext.builder()
                .addProperty("tenantId", featureContextImpl.getTenantId());

        if (featureContextImpl.getCompanyId() != null) { //TODO sacar esto, siempre viene tenant+company
            contextBuilder.addProperty("companyId", featureContextImpl.getCompanyId());
        }

        UnleashContext context = contextBuilder.build();
        return unleash.isEnabled(toggleName.getToggleName(), context);
    }

    public boolean isFeatureToggleActive(TogglesNames toggleName, Map<String, String> properties) {
        UnleashContext.Builder contextBuilder = UnleashContext
                .builder();
        properties.forEach(contextBuilder::addProperty);
        UnleashContext context = contextBuilder.build();
        return unleash.isEnabled(toggleName.getToggleName(), context);
    }

}

