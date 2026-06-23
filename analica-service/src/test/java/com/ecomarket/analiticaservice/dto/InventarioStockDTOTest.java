package com.ecomarket.analiticaservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class InventarioStockDTOTest {

    @Test
    void constructorAndGettersWork() {
        InventarioStockDTO dto = new InventarioStockDTO(1L, 100L, 50, "Sucursal Central");

        assertEquals(1L, dto.getId());
        assertEquals(100L, dto.getProductoId());
        assertEquals(50, dto.getCantidadDisponible());
        assertEquals("Sucursal Central", dto.getSucursal());
    }
}
