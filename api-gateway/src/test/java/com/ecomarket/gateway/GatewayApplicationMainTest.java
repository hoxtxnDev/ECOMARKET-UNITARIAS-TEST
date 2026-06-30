package com.ecomarket.gateway;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

class GatewayApplicationMainTest {

    @Test
    void mainStartsApplication() {
        try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {
            ConfigurableApplicationContext ctx = mock(ConfigurableApplicationContext.class);
            mocked.when(() -> SpringApplication.run(GatewayApplication.class, new String[]{})).thenReturn(ctx);

            assertDoesNotThrow(() -> GatewayApplication.main(new String[]{}));

            mocked.verify(() -> SpringApplication.run(GatewayApplication.class, new String[]{}));
        }
    }
}
