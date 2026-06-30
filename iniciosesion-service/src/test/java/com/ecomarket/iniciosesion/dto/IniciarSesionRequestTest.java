package com.ecomarket.iniciosesion.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class IniciarSesionRequestTest {

    @Test
    void settersWork() {
        IniciarSesionRequest dto = new IniciarSesionRequest();
        dto.setCorreo("user@email.com");
        dto.setContrasena("pass");

        assertEquals("user@email.com", dto.getCorreo());
        assertEquals("pass", dto.getContrasena());
    }
}
