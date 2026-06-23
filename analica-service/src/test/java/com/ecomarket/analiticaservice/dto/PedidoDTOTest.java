package com.ecomarket.analiticaservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class PedidoDTOTest {

    @Test
    void constructorAndGettersWork() {
        LocalDateTime now = LocalDateTime.now();
        PedidoDTO dto = new PedidoDTO(1L, 10L, 250.0, now, "PENDIENTE");

        assertEquals(1L, dto.getId());
        assertEquals(10L, dto.getClienteId());
        assertEquals(250.0, dto.getTotal());
        assertEquals(now, dto.getFechaPedido());
        assertEquals("PENDIENTE", dto.getEstado());
    }
}
