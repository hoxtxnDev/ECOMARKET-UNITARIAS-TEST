package com.ecomarket.iniciosesion.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CrearCredencialRequestTest {

    @Test
    void settersWork() {
        CrearCredencialRequest dto = new CrearCredencialRequest();
        dto.setUsuarioId(1L);
        dto.setCorreo("user@email.com");
        dto.setContrasena("password123");
        dto.setRol("ADMIN");

        assertEquals(1L, dto.getUsuarioId());
        assertEquals("user@email.com", dto.getCorreo());
        assertEquals("password123", dto.getContrasena());
        assertEquals("ADMIN", dto.getRol());
    }
}
