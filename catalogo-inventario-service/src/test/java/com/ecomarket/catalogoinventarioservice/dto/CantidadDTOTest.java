package com.ecomarket.catalogoinventarioservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CantidadDTOTest {

    @Test
    void constructorAndGettersWork() {
        CantidadDTO dto = new CantidadDTO(1L, 5L, 10);

        assertEquals(1L, dto.getProductoId());
        assertEquals(5L, dto.getSucursalId());
        assertEquals(10, dto.getCantidad());
    }
}
