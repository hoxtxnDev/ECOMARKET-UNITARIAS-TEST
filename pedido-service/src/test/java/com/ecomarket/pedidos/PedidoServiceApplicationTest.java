package com.ecomarket.pedidos;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class PedidoServiceApplicationTests {

    @Test
    @DisplayName("El contexto de Spring Boot carga correctamente")
    void contextLoads() {}

    @Test
    @DisplayName("El método main se ejecuta correctamente")
    void mainTest() {
        PedidoServiceApplication.main(new String[] {
            "--spring.main.web-application-type=none",
            "--spring.profiles.active=test"
        });
    }
}
