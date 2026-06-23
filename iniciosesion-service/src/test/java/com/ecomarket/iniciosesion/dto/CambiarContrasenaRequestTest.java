package com.ecomarket.iniciosesion.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CambiarContrasenaRequestTest {

    @Test
    void settersWork() {
        CambiarContrasenaRequest dto = new CambiarContrasenaRequest();
        dto.setUsuarioId(1L);
        dto.setContrasenaActual("old-pass");
        dto.setNuevaContrasena("new-pass");

        assertEquals(1L, dto.getUsuarioId());
        assertEquals("old-pass", dto.getContrasenaActual());
        assertEquals("new-pass", dto.getNuevaContrasena());
    }
}
