package com.ecomarket.catalogoinventarioservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CantidadGlobalDTOTest {

    @Test
    void constructorAndGettersWork() {
        CantidadGlobalDTO dto = new CantidadGlobalDTO(1L, 50);

        assertEquals(1L, dto.getProductoId());
        assertEquals(50, dto.getCantidad());
    }
}
