package com.ecomarket.carritocompraservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SeleccionRequestDTOTest {

    @Test
    void noArgsConstructor() {
        SeleccionRequestDTO dto = new SeleccionRequestDTO();
        assertNull(dto.getId());
    }

    @Test
    void allArgsConstructor() {
        SeleccionRequestDTO dto = new SeleccionRequestDTO(100L);
        assertEquals(100L, dto.getId());
    }

    @Test
    void settersWork() {
        SeleccionRequestDTO dto = new SeleccionRequestDTO();
        dto.setId(200L);
        assertEquals(200L, dto.getId());
    }

    @Test
    void testToString() {
        SeleccionRequestDTO dto = new SeleccionRequestDTO(100L);
        String str = dto.toString();
        assertNotNull(str);
        assertTrue(str.contains("id"));
    }

    @Test
    void testEquals() {
        SeleccionRequestDTO dto1 = new SeleccionRequestDTO(100L);
        SeleccionRequestDTO dto2 = new SeleccionRequestDTO(100L);
        SeleccionRequestDTO dto3 = new SeleccionRequestDTO(200L);
        
        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertEquals(dto1, dto1);
        assertNotEquals(dto1, null);
        assertNotEquals(dto1, "string");
    }

    @Test
    void testHashCode() {
        SeleccionRequestDTO dto1 = new SeleccionRequestDTO(100L);
        SeleccionRequestDTO dto2 = new SeleccionRequestDTO(100L);
        
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }
}
