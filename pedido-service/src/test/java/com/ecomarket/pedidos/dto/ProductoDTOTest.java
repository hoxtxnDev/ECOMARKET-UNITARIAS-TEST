package com.ecomarket.pedidos.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ProductoDTOTest {

    @Test
    void constructorAndGettersWork() {
        ProductoDTO dto = new ProductoDTO(1L, "SKU001", "Laptop", 1500.0);

        assertEquals(1L, dto.getId());
        assertEquals("SKU001", dto.getSku());
        assertEquals("Laptop", dto.getNombre());
        assertEquals(1500.0, dto.getPrecioBase());
    }
}
