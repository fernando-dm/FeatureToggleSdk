package com.company.config.unleash;

import com.company.config.unleash.utils.UnleashSocketConnector;
import io.getunleash.Unleash;
import io.getunleash.util.UnleashConfig;
import java.lang.reflect.Field;
import java.net.Socket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UnleashConfigurationTest {

    private String appName = "app-name";
    private String instanceId = "1";
    private String apiUrl = "http://localhost:4242/api";
    private String clientSecret = "default:development.1ee5fb49a9f0f124853a2deee73a2da98d0ee36846ea400eff06e1fc";
    private String toggleFakeActive = "fakeFeatureFlag";
    private String toggleActive = "unleash";

    @InjectMocks
    private UnleashConfiguration unleashConfiguration;
    private UnleashSocketConnector mockUnleashSocketConnector;

    private UnleashConfig mockUnleashConfig;
    private Socket mockSocket;


    @BeforeEach
    void setUp() {
        // Create the mock objects
        MockitoAnnotations.openMocks(this);
        mockSocket = mock(Socket.class);
        mockUnleashSocketConnector = mock(UnleashSocketConnector.class);
        mockUnleashConfig = mock(UnleashConfig.class);

    }

    @Test
    void unleashStartFakeUnleash() throws NoSuchFieldException, IllegalAccessException {
        setPrivateField(unleashConfiguration, "toggleActive", toggleFakeActive);
        Unleash unleashConf = unleashConfiguration.unleash();
        assertTrue(unleashConf instanceof FakeUnleash);
    }

    @Test
    void unleashStartWithLocalCache() throws NoSuchFieldException, IllegalAccessException {
        setPrivateField(unleashConfiguration, "toggleActive", toggleActive);
        setPrivateField(unleashConfiguration, "appName", appName);
        setPrivateField(unleashConfiguration, "instanceId", instanceId);
        setPrivateField(unleashConfiguration, "apiUrl", apiUrl);
        setPrivateField(unleashConfiguration, "clientSecret", clientSecret);

        Unleash unleashConf = unleashConfiguration.unleash();

        assertFalse(unleashConf instanceof FakeUnleash);
        assertTrue(unleashConf instanceof Unleash);
    }

    private void setPrivateField(Object object, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }
}