package com.ecomarket.carritocompraservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MetodoPagoDTOTest {

    @Test
    void noArgsConstructor() {
        MetodoPagoDTO dto = new MetodoPagoDTO();
        assertNull(dto.getId());
        assertNull(dto.getNombre());
    }

    @Test
    void allArgsConstructor() {
        MetodoPagoDTO dto = new MetodoPagoDTO(1L, "Tarjeta Crédito");
        assertEquals(1L, dto.getId());
        assertEquals("Tarjeta Crédito", dto.getNombre());
    }

    @Test
    void settersWork() {
        MetodoPagoDTO dto = new MetodoPagoDTO();
        dto.setId(2L);
        dto.setNombre("Transferencia");

        assertEquals(2L, dto.getId());
        assertEquals("Transferencia", dto.getNombre());
    }

    @Test
    void testToString() {
        MetodoPagoDTO dto = new MetodoPagoDTO(1L, "Tarjeta Crédito");
        String str = dto.toString();
        assertNotNull(str);
        assertTrue(str.contains("id") && str.contains("nombre"));
    }

    @Test
    void testEquals() {
        MetodoPagoDTO dto1 = new MetodoPagoDTO(1L, "Tarjeta Crédito");
        MetodoPagoDTO dto2 = new MetodoPagoDTO(1L, "Tarjeta Crédito");
        MetodoPagoDTO dto3 = new MetodoPagoDTO(2L, "Transferencia");
        
        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertEquals(dto1, dto1);
    }

    @Test
    void testHashCode() {
        MetodoPagoDTO dto1 = new MetodoPagoDTO(1L, "Tarjeta Crédito");
        MetodoPagoDTO dto2 = new MetodoPagoDTO(1L, "Tarjeta Crédito");
        
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }
}
