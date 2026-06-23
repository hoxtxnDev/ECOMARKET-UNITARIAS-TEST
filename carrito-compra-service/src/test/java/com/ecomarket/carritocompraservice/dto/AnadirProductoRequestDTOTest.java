package com.ecomarket.carritocompraservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class AnadirProductoRequestDTOTest {

    @Test
    void constructorAndGettersWork() {
        AnadirProductoRequestDTO dto = new AnadirProductoRequestDTO(1L, 100L, 3);

        assertEquals(1L, dto.getUsuarioId());
        assertEquals(100L, dto.getProductoId());
        assertEquals(3, dto.getCantidad());
    }
}
