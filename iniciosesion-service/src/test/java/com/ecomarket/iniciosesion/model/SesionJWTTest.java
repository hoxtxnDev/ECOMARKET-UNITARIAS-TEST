package com.ecomarket.iniciosesion.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class SesionJWTTest {

    @Test
    void builderAndGettersSettersWork() {
        LocalDateTime emision = LocalDateTime.now();
        LocalDateTime expiracion = emision.plusHours(1);
        SesionJWT s = SesionJWT.builder()
                .id(1L)
                .token("jwt.token.here")
                .usuarioId(10L)
                .rolAcceso("ADMIN")
                .fechaEmision(emision)
                .fechaExpiracion(expiracion)
                .build();
        assertEquals(1L, s.getId());
        assertEquals("jwt.token.here", s.getToken());
        assertEquals(10L, s.getUsuarioId());
        assertEquals("ADMIN", s.getRolAcceso());
        assertEquals(emision, s.getFechaEmision());
        assertEquals(expiracion, s.getFechaExpiracion());
    }

    @Test
    void allArgsConstructorWorks() {
        LocalDateTime now = LocalDateTime.now();
        SesionJWT s = new SesionJWT(1L, "token", 10L, "ADMIN", now, now.plusHours(2));
        assertEquals("token", s.getToken());
        assertEquals(10L, s.getUsuarioId());
    }
}
