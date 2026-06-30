package com.ecomarket.envioservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PedidoDTOTest {

    @Test
    void settersWork() {
        PedidoDTO dto = new PedidoDTO();
        dto.setId(1L);
        dto.setClienteId(5L);
        dto.setDireccionEnvioId(100L);
        dto.setSubtotal(500.0);
        dto.setTotal(550.0);

        assertEquals(1L, dto.getId());
        assertEquals(5L, dto.getClienteId());
        assertEquals(100L, dto.getDireccionEnvioId());
        assertEquals(500.0, dto.getSubtotal());
        assertEquals(550.0, dto.getTotal());
    }
}
