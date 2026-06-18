package com.ecomarket.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GatewayApplicationTest {

    @Test
    void contextLoads() {
    }

    @Test
    void mainStartsApplication() {
        GatewayApplication.main(new String[]{});
    }
}
