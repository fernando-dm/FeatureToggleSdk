package com.company.toggles;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


class TogglesNamesEnumTest {

    @Test
    @DisplayName("Should return toggle name")
    void shouldReturnToggleName() {
        // Given
        String expectedToggleName = "tenantCompanyToggle";
        TogglesNamesEnum toggle = TogglesNamesEnum.TENANT_COMPANY_TOGGLE;

        // When
        String actualToggleName = toggle.getToggleName();

        // Then
        assertEquals(actualToggleName, expectedToggleName);
    }
}
