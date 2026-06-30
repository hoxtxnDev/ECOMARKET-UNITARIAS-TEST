package com.ecomarket.iniciosesion.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class TokenRecuperacionTest {

    @Test
    void builderAndGettersSettersWork() {
        Credencial credencial = new Credencial();
        LocalDateTime expiracion = LocalDateTime.now().plusDays(1);
        TokenRecuperacion t = TokenRecuperacion.builder()
                .id(1L)
                .credencial(credencial)
                .codigoAlfanumerico("abc123def")
                .expiracion(expiracion)
                .consumido(true)
                .build();
        assertEquals(1L, t.getId());
        assertEquals(credencial, t.getCredencial());
        assertEquals("abc123def", t.getCodigoAlfanumerico());
        assertEquals(expiracion, t.getExpiracion());
        assertTrue(t.getConsumido());
    }

    @Test
    void consumidoDefaultIsFalse() {
        TokenRecuperacion t = new TokenRecuperacion();
        assertFalse(t.getConsumido());
    }
}
