package com.ecomarket.carritocompraservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MetodoPagoDTOTest {

    @Test
    void constructorAndGettersWork() {
        MetodoPagoDTO dto = new MetodoPagoDTO(1L, "TARJETA");

        assertEquals(1L, dto.getId());
        assertEquals("TARJETA", dto.getNombre());
    }
}
