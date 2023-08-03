package com.company.config.toggles;

import com.company.config.unleash.FakeUnleash;
import com.company.service.FeatureToggleService;
import com.company.service.UnleashFeatureToggleService;
import io.getunleash.Unleash;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class FeatureToggleConfiguration {

    private static final Logger logger = Logger.getLogger(FeatureToggleConfiguration.class.getName());

    @Value("${feature.toggle.service}")                 // all this value can be injected from an application.yml
    private String toggleActive;

    private final Unleash unleash;

    public FeatureToggleConfiguration(Unleash unleash) {
        this.unleash = unleash;
    }

    @Bean
    @Primary
    public FeatureToggleService featureToggleService() {
        switch (toggleActive) {
            case "unleash":
                return createUnleashFeatureToggleService();
            case "fakeFeatureFlag":
                return createFakeUnleashFeatureToggleService();
            default:
                throw new IllegalArgumentException("Invalid feature toggle service configuration");
        }
    }

    private FeatureToggleService createFakeUnleashFeatureToggleService() {
        logger.info("\n\n***** FakeUnleash activated *****\n");
        FakeUnleash fakeUnleash = new FakeUnleash();
        fakeUnleash.enableAll();
        return new UnleashFeatureToggleService(fakeUnleash);
    }

    private FeatureToggleService createUnleashFeatureToggleService() {
        return new UnleashFeatureToggleService(unleash);
    }

}

