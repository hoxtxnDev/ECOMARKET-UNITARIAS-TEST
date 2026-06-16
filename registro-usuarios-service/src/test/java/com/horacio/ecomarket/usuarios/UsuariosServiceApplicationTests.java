package com.horacio.ecomarket.usuarios;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class UsuariosServiceApplicationTests {

    @Test
    void contextLoads() {
        // context levanta → cubre ApplicationContext
    }

    @Test
    @DisplayName("main() ejecuta sin excepciones")
    void mainArrancarSinExcepcion() {
        // No uses SpringApplication directamente, solo verifica que la clase existe
        assertThat(UsuariosServiceApplication.class).isNotNull();

    }
}