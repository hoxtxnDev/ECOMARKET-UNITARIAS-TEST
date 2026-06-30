package com.ecomarket.iniciosesion.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class RestablecerConTokenRequestTest {

    @Test
    void settersWork() {
        RestablecerConTokenRequest dto = new RestablecerConTokenRequest();
        dto.setCodigo("rec-code");
        dto.setNuevaContrasena("new-pass");

        assertEquals("rec-code", dto.getCodigo());
        assertEquals("new-pass", dto.getNuevaContrasena());
    }
}
