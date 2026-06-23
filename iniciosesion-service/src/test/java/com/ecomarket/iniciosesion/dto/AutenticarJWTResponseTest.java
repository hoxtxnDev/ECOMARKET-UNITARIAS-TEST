package com.ecomarket.iniciosesion.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

class AutenticarJWTResponseTest {

    @Test
    void builderCreatesResponse() {
        AutenticarJWTResponse dto = AutenticarJWTResponse.builder()
                .valido(true)
                .usuarioId(1L)
                .correo("user@email.com")
                .roles(List.of("ADMIN"))
                .build();

        assertTrue(dto.isValido());
        assertEquals(1L, dto.getUsuarioId());
        assertEquals("user@email.com", dto.getCorreo());
        assertEquals(1, dto.getRoles().size());
    }
}
