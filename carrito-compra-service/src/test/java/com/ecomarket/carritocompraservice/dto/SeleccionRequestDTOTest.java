package com.ecomarket.carritocompraservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SeleccionRequestDTOTest {

    @Test
    void constructorAndGettersWork() {
        SeleccionRequestDTO dto = new SeleccionRequestDTO(1L);

        assertEquals(1L, dto.getId());
    }
}
