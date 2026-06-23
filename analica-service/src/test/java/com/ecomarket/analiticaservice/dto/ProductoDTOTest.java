package com.ecomarket.analiticaservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ProductoDTOTest {

    @Test
    void constructorAndGettersWork() {
        ProductoDTO dto = new ProductoDTO(1L, "Laptop", 1500.0, "Electrónica");

        assertEquals(1L, dto.getId());
        assertEquals("Laptop", dto.getNombre());
        assertEquals(1500.0, dto.getPrecio());
        assertEquals("Electrónica", dto.getCategoria());
    }
}
