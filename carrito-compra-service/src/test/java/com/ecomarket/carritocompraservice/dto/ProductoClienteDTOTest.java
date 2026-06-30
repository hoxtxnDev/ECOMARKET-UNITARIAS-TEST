package com.ecomarket.carritocompraservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ProductoClienteDTOTest {

    @Test
    void noArgsConstructor() {
        ProductoClienteDTO dto = new ProductoClienteDTO();
        assertNull(dto.getId());
        assertNull(dto.getSku());
        assertNull(dto.getNombre());
        assertNull(dto.getPrecioBase());
        assertNull(dto.getDescripcion());
        assertNull(dto.getImagenUrl());
    }

    @Test
    void allArgsConstructor() {
        ProductoClienteDTO dto = new ProductoClienteDTO(1L, "SKU123", "Producto", 99.99, "Descripción", "http://imagen.jpg");
        assertEquals(1L, dto.getId());
        assertEquals("SKU123", dto.getSku());
        assertEquals("Producto", dto.getNombre());
        assertEquals(99.99, dto.getPrecioBase());
        assertEquals("Descripción", dto.getDescripcion());
        assertEquals("http://imagen.jpg", dto.getImagenUrl());
    }

    @Test
    void settersWork() {
        ProductoClienteDTO dto = new ProductoClienteDTO();
        dto.setId(1L);
        dto.setSku("SKU123");
        dto.setNombre("Producto");
        dto.setPrecioBase(99.99);
        dto.setDescripcion("Descripción");
        dto.setImagenUrl("http://imagen.jpg");

        assertEquals(1L, dto.getId());
        assertEquals("SKU123", dto.getSku());
        assertEquals("Producto", dto.getNombre());
        assertEquals(99.99, dto.getPrecioBase());
        assertEquals("Descripción", dto.getDescripcion());
        assertEquals("http://imagen.jpg", dto.getImagenUrl());
    }

    @Test
    void testToString() {
        ProductoClienteDTO dto = new ProductoClienteDTO(1L, "SKU123", "Producto", 99.99, "Descripción", "http://imagen.jpg");
        String str = dto.toString();
        assertNotNull(str);
        assertTrue(str.contains("id"));
    }

    @Test
    void testEquals() {
        ProductoClienteDTO dto1 = new ProductoClienteDTO(1L, "SKU123", "Producto", 99.99, "Desc", "http://img.jpg");
        ProductoClienteDTO dto2 = new ProductoClienteDTO(1L, "SKU123", "Producto", 99.99, "Desc", "http://img.jpg");
        ProductoClienteDTO dto3 = new ProductoClienteDTO(2L, "SKU456", "Otro", 49.99, "Otra", "http://otro.jpg");
        
        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertEquals(dto1, dto1);
    }

    @Test
    void testHashCode() {
        ProductoClienteDTO dto1 = new ProductoClienteDTO(1L, "SKU123", "Producto", 99.99, "Desc", "http://img.jpg");
        ProductoClienteDTO dto2 = new ProductoClienteDTO(1L, "SKU123", "Producto", 99.99, "Desc", "http://img.jpg");
        
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }
}
