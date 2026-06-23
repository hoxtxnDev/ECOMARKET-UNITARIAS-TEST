package com.ecomarket.catalogoinventarioservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ProductoRequestDTOTest {

    @Test
    void builderCreatesProductoRequest() {
        ProductoRequestDTO dto = ProductoRequestDTO.builder()
                .sku("SKU001")
                .nombre("Laptop")
                .descripcion("Laptop gaming")
                .precioBase(1500.0)
                .categoriaId(1L)
                .estadoId(1L)
                .imagenUrl("img.jpg")
                .build();

        assertEquals("SKU001", dto.getSku());
        assertEquals("Laptop", dto.getNombre());
        assertEquals(1500.0, dto.getPrecioBase());
        assertEquals(1L, dto.getCategoriaId());
        assertEquals(1L, dto.getEstadoId());
        assertEquals("img.jpg", dto.getImagenUrl());
    }
}
