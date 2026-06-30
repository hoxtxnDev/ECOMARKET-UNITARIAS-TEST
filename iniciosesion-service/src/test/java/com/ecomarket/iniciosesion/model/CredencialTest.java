package com.ecomarket.iniciosesion.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CredencialTest {

    @Test
    void builderAndGettersSettersWork() {
        Credencial c = Credencial.builder()
                .id(1L)
                .usuarioId(10L)
                .correoAcceso("test@test.com")
                .contrasenaHash("hash123")
                .cuentaBloqueada(true)
                .rolAcceso("ADMIN")
                .build();
        assertEquals(1L, c.getId());
        assertEquals(10L, c.getUsuarioId());
        assertEquals("test@test.com", c.getCorreoAcceso());
        assertEquals("hash123", c.getContrasenaHash());
        assertTrue(c.getCuentaBloqueada());
        assertEquals("ADMIN", c.getRolAcceso());
    }

    @Test
    void cuentaBloqueadaDefaultIsFalse() {
        Credencial c = new Credencial();
        assertFalse(c.getCuentaBloqueada());
    }
}
