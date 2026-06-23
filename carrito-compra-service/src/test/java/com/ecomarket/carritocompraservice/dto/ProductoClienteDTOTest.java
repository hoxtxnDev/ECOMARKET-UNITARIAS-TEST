package com.ecomarket.carritocompraservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ProductoClienteDTOTest {

    @Test
    void constructorAndGettersWork() {
        ProductoClienteDTO dto = new ProductoClienteDTO(1L, "SKU001", "Laptop", 1500.0, "Laptop gaming", "img.jpg");

        assertEquals(1L, dto.getId());
        assertEquals("SKU001", dto.getSku());
        assertEquals("Laptop", dto.getNombre());
        assertEquals(1500.0, dto.getPrecioBase());
        assertEquals("Laptop gaming", dto.getDescripcion());
        assertEquals("img.jpg", dto.getImagenUrl());
    }
}
