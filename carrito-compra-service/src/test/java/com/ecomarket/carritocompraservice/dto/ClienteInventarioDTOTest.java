package com.ecomarket.carritocompraservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ClienteInventarioDTOTest {

    @Test
    void noArgsConstructor() {
        ClienteInventarioDTO dto = new ClienteInventarioDTO();
        assertNull(dto.getId());
        assertNull(dto.getProductoId());
        assertNull(dto.getSucursalId());
        assertNull(dto.getCantidadDisponible());
        assertNull(dto.getCantidadReservada());
        assertNull(dto.getStockMinimoAlerta());
    }

    @Test
    void allArgsConstructor() {
        ClienteInventarioDTO dto = new ClienteInventarioDTO(1L, 10L, 5L, 50, 10, 20);
        assertEquals(1L, dto.getId());
        assertEquals(10L, dto.getProductoId());
        assertEquals(5L, dto.getSucursalId());
        assertEquals(50, dto.getCantidadDisponible());
        assertEquals(10, dto.getCantidadReservada());
        assertEquals(20, dto.getStockMinimoAlerta());
    }

    @Test
    void settersWork() {
        ClienteInventarioDTO dto = new ClienteInventarioDTO();
        dto.setId(1L);
        dto.setProductoId(10L);
        dto.setSucursalId(5L);
        dto.setCantidadDisponible(50);
        dto.setCantidadReservada(10);
        dto.setStockMinimoAlerta(20);

        assertEquals(1L, dto.getId());
        assertEquals(10L, dto.getProductoId());
        assertEquals(5L, dto.getSucursalId());
        assertEquals(50, dto.getCantidadDisponible());
        assertEquals(10, dto.getCantidadReservada());
        assertEquals(20, dto.getStockMinimoAlerta());
    }

    @Test
    void testToString() {
        ClienteInventarioDTO dto = new ClienteInventarioDTO(1L, 10L, 5L, 50, 10, 20);
        String str = dto.toString();
        assertNotNull(str);
        assertTrue(str.contains("id"));
    }

    @Test
    void testEquals() {
        ClienteInventarioDTO dto1 = new ClienteInventarioDTO(1L, 10L, 5L, 50, 10, 20);
        ClienteInventarioDTO dto2 = new ClienteInventarioDTO(1L, 10L, 5L, 50, 10, 20);
        ClienteInventarioDTO dto3 = new ClienteInventarioDTO(2L, 11L, 6L, 60, 20, 30);
        
        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertEquals(dto1, dto1);
    }

    @Test
    void testHashCode() {
        ClienteInventarioDTO dto1 = new ClienteInventarioDTO(1L, 10L, 5L, 50, 10, 20);
        ClienteInventarioDTO dto2 = new ClienteInventarioDTO(1L, 10L, 5L, 50, 10, 20);
        
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }
}
