package com.ecomarket.carritocompraservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class AnadirProductoRequestDTOTest {

    @Test
    void noArgsConstructor() {
        AnadirProductoRequestDTO dto = new AnadirProductoRequestDTO();
        assertNull(dto.getProductoId());
        assertNull(dto.getCantidad());
    }

    @Test
    void allArgsConstructor() {
        AnadirProductoRequestDTO dto = new AnadirProductoRequestDTO(100L, 3);
        assertEquals(100L, dto.getProductoId());
        assertEquals(3, dto.getCantidad());
    }

    @Test
    void settersWork() {
        AnadirProductoRequestDTO dto = new AnadirProductoRequestDTO();
        dto.setProductoId(200L);
        dto.setCantidad(5);
        assertEquals(200L, dto.getProductoId());
        assertEquals(5, dto.getCantidad());
    }

    @Test
    void testToString() {
        AnadirProductoRequestDTO dto = new AnadirProductoRequestDTO(100L, 3);
        String str = dto.toString();
        assertNotNull(str);
        assertTrue(str.contains("productoId") && str.contains("cantidad"));
    }

    @Test
    void testEquals() {
        AnadirProductoRequestDTO dto1 = new AnadirProductoRequestDTO(100L, 3);
        AnadirProductoRequestDTO dto2 = new AnadirProductoRequestDTO(100L, 3);
        AnadirProductoRequestDTO dto3 = new AnadirProductoRequestDTO(200L, 5);
        
        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertEquals(dto1, dto1);
        assertNotEquals(dto1, null);
        assertNotEquals(dto1, "string");
    }

    @Test
    void testHashCode() {
        AnadirProductoRequestDTO dto1 = new AnadirProductoRequestDTO(100L, 3);
        AnadirProductoRequestDTO dto2 = new AnadirProductoRequestDTO(100L, 3);
        
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }
}
