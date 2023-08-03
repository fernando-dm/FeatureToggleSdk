package com.company.config.unleash;

import com.company.config.unleash.utils.UnleashSocketConnector;
import io.getunleash.DefaultUnleash;
import io.getunleash.Unleash;
import io.getunleash.util.UnleashConfig;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "feature")
public class UnleashConfiguration {
    private static final Logger logger = Logger.getLogger(UnleashConfiguration.class.getName());

    @Value("${feature.toggle.appName}")
    private String appName;

    @Value("${feature.toggle.apiUrl}")
    private String apiUrl;

    @Value("${feature.toggle.instanceId}")
    String instanceId;

    @Value("${feature.toggle.clientSecret}")
    String clientSecret;

    @Value("${feature.toggle.service}")
    private String toggleActive;

    private final UnleashSocketConnector socketConnector;

    public UnleashConfiguration() {
        socketConnector = new UnleashSocketConnector();
    }

    @Bean
    public Unleash unleash() {
        if ("fakeFeatureFlag".equalsIgnoreCase(toggleActive)) {
            return new FakeUnleash();
        } else {
            UnleashConfig config = new UnleashConfig.Builder()
                    .appName(appName)
                    .instanceId(instanceId)
                    .unleashAPI(apiUrl)
                    .apiKey(clientSecret)
                    .build();

            return createDefaultUnleash(config);
        }
    }

    private Unleash createDefaultUnleash(UnleashConfig config) {
        if (socketConnector.isUnleashServerReachable(config.getUnleashAPI())) {
            return new DefaultUnleash(config);
        } else {
            logger.severe("Unleash server is down or inaccessible.");
            return createFallbackUnleash(config);
        }
    }

    private Unleash createFallbackUnleash(UnleashConfig config) {
        return new DefaultUnleash(config);
    }

}
