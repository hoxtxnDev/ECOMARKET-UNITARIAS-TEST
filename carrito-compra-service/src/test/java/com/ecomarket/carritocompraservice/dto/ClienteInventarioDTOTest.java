package com.ecomarket.carritocompraservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ClienteInventarioDTOTest {

    @Test
    void constructorAndGettersWork() {
        ClienteInventarioDTO dto = new ClienteInventarioDTO(1L, 100L, 5L, 20, 5, 3);

        assertEquals(1L, dto.getId());
        assertEquals(100L, dto.getProductoId());
        assertEquals(5L, dto.getSucursalId());
        assertEquals(20, dto.getCantidadDisponible());
        assertEquals(5, dto.getCantidadReservada());
        assertEquals(3, dto.getStockMinimoAlerta());
    }
}
