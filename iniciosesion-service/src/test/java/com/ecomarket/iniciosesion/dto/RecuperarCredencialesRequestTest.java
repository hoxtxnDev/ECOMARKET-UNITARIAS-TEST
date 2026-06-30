package com.ecomarket.iniciosesion.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class RecuperarCredencialesRequestTest {

    @Test
    void settersWork() {
        RecuperarCredencialesRequest dto = new RecuperarCredencialesRequest();
        dto.setCorreo("user@email.com");

        assertEquals("user@email.com", dto.getCorreo());
    }
}
