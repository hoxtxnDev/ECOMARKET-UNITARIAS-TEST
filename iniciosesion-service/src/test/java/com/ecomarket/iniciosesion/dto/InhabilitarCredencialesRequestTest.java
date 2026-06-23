package com.ecomarket.iniciosesion.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class InhabilitarCredencialesRequestTest {

    @Test
    void settersWork() {
        InhabilitarCredencialesRequest dto = new InhabilitarCredencialesRequest();
        dto.setUsuarioId(1L);

        assertEquals(1L, dto.getUsuarioId());
    }
}
