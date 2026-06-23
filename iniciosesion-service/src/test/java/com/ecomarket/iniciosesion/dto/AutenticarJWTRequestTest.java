package com.ecomarket.iniciosesion.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class AutenticarJWTRequestTest {

    @Test
    void settersWork() {
        AutenticarJWTRequest dto = new AutenticarJWTRequest();
        dto.setToken("jwt-token");

        assertEquals("jwt-token", dto.getToken());
    }
}
