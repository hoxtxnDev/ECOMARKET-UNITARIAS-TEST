package com.ecomarket.gateway.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.RouteLocator;

@SpringBootTest
class RouteConfigurationTest {

    @Autowired
    private RouteLocator routeLocator;

    @Test
    void routeLocatorIsConfigured() {
        assertThat(routeLocator).isNotNull();
    }
}
