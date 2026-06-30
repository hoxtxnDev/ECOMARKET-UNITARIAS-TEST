package com.ecomarket.iniciosesion.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class IniciarSesionResponseTest {

    @Test
    void builderCreatesResponse() {
        IniciarSesionResponse dto = IniciarSesionResponse.builder()
                .token("jwt-token")
                .usuarioId(1L)
                .correo("user@email.com")
                .rol("ADMIN")
                .expiracionMs(3600000L)
                .build();

        assertEquals("jwt-token", dto.getToken());
        assertEquals(1L, dto.getUsuarioId());
        assertEquals("user@email.com", dto.getCorreo());
        assertEquals("ADMIN", dto.getRol());
        assertEquals(3600000L, dto.getExpiracionMs());
    }
}
