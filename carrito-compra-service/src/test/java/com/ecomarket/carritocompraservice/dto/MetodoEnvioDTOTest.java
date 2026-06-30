package com.ecomarket.carritocompraservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MetodoEnvioDTOTest {

    @Test
    void noArgsConstructor() {
        MetodoEnvioDTO dto = new MetodoEnvioDTO();
        assertNull(dto.getId());
        assertNull(dto.getNombre());
    }

    @Test
    void allArgsConstructor() {
        MetodoEnvioDTO dto = new MetodoEnvioDTO(1L, "Express");
        assertEquals(1L, dto.getId());
        assertEquals("Express", dto.getNombre());
    }

    @Test
    void settersWork() {
        MetodoEnvioDTO dto = new MetodoEnvioDTO();
        dto.setId(2L);
        dto.setNombre("Standard");

        assertEquals(2L, dto.getId());
        assertEquals("Standard", dto.getNombre());
    }

    @Test
    void testToString() {
        MetodoEnvioDTO dto = new MetodoEnvioDTO(1L, "Express");
        String str = dto.toString();
        assertNotNull(str);
        assertTrue(str.contains("id") && str.contains("nombre"));
    }

    @Test
    void testEquals() {
        MetodoEnvioDTO dto1 = new MetodoEnvioDTO(1L, "Express");
        MetodoEnvioDTO dto2 = new MetodoEnvioDTO(1L, "Express");
        MetodoEnvioDTO dto3 = new MetodoEnvioDTO(2L, "Standard");
        
        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertEquals(dto1, dto1);
    }

    @Test
    void testHashCode() {
        MetodoEnvioDTO dto1 = new MetodoEnvioDTO(1L, "Express");
        MetodoEnvioDTO dto2 = new MetodoEnvioDTO(1L, "Express");
        
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }
}
