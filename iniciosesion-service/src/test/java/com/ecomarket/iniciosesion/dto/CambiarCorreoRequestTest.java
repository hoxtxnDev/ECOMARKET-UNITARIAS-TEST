package com.ecomarket.iniciosesion.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CambiarCorreoRequestTest {

    @Test
    void settersWork() {
        CambiarCorreoRequest dto = new CambiarCorreoRequest();
        dto.setUsuarioId(1L);
        dto.setNuevoCorreo("nuevo@email.com");
        dto.setContrasenaActual("pass");

        assertEquals(1L, dto.getUsuarioId());
        assertEquals("nuevo@email.com", dto.getNuevoCorreo());
        assertEquals("pass", dto.getContrasenaActual());
    }
}
