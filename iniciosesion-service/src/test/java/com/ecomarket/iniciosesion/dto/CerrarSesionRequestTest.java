package com.ecomarket.iniciosesion.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CerrarSesionRequestTest {

    @Test
    void settersWork() {
        CerrarSesionRequest dto = new CerrarSesionRequest();
        dto.setToken("jwt-token");

        assertEquals("jwt-token", dto.getToken());
    }
}
