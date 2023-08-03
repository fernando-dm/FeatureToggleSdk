package com.company.service;

import com.company.context.FeatureContextImpl;
import com.company.toggles.TogglesNames;
import io.getunleash.Unleash;
import io.getunleash.UnleashContext;
import io.getunleash.UnleashException;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UnleashFeatureToggleService implements FeatureToggleService {
    @Value("${feature.toggle.appName}")
    private String appName;
    private final Unleash unleash;

    private static final Logger logger = Logger.getLogger(UnleashFeatureToggleService.class.getName());

    public UnleashFeatureToggleService(Unleash unleash) {
        this.unleash = unleash;
    }

    @Override
    public boolean isFeatureToggleActive(TogglesNames toggleName, FeatureContextImpl featureContextImpl) {

        try {
            String companyId = Optional.ofNullable(featureContextImpl.getCompanyId()).orElse("N/A");

            logger.info(String.format("Using feature toggle: %s for appName: %s tenant: %s and company: %s",
                    toggleName.getToggleName(), appName, featureContextImpl.getTenantId(), companyId));

            UnleashContext.Builder contextBuilder = UnleashContext.builder()
                    .addProperty("appName", appName)
                    .addProperty("tenantId", featureContextImpl.getTenantId());

            if (!"N/A".equals(companyId))
                contextBuilder.addProperty("companyId", companyId);

            UnleashContext context = contextBuilder.build();
            return unleash.isEnabled(toggleName.getToggleName(), context);
        } catch (UnleashException e) {
            logger.severe("Error with SDK Unleash: " + e.getMessage());
            return false;
        }
    }

    public boolean isFeatureToggleActive(TogglesNames toggleName, Map<String, String> properties) {
        try {
            UnleashContext.Builder contextBuilder = UnleashContext
                    .builder()
                    .addProperty("appName", appName);

            properties.forEach(contextBuilder::addProperty);

            logger.info(String.format("Using feature toggle: %s with properties: %s",
                    toggleName.getToggleName(), properties));

            UnleashContext context = contextBuilder.build();
            return unleash.isEnabled(toggleName.getToggleName(), context);
        } catch (UnleashException e) {
            logger.severe("Error with SDK Unleash: " + e.getMessage());
            return false;
        }
    }
}

