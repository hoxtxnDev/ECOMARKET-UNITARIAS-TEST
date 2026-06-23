package com.ecomarket.analiticaservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class TicketSoporteDTOTest {

    @Test
    void constructorAndGettersWork() {
        LocalDateTime now = LocalDateTime.now();
        TicketSoporteDTO dto = new TicketSoporteDTO(1L, 10L, "Problema con pedido", "RECLAMO", "ABIERTO", now);

        assertEquals(1L, dto.getId());
        assertEquals(10L, dto.getClienteId());
        assertEquals("Problema con pedido", dto.getAsunto());
        assertEquals("RECLAMO", dto.getCategoria());
        assertEquals("ABIERTO", dto.getEstado());
        assertEquals(now, dto.getFechaCreacion());
    }
}
