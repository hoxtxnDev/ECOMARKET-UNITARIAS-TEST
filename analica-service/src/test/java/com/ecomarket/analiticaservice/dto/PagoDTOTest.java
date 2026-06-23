package com.ecomarket.analiticaservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class PagoDTOTest {

    @Test
    void constructorAndGettersWork() {
        LocalDateTime now = LocalDateTime.now();
        PagoDTO dto = new PagoDTO(1L, 10L, 500.0, "TARJETA", "COMPLETADO", now);

        assertEquals(1L, dto.getId());
        assertEquals(10L, dto.getPedidoId());
        assertEquals(500.0, dto.getMonto());
        assertEquals("TARJETA", dto.getMetodoPago());
        assertEquals("COMPLETADO", dto.getEstado());
        assertEquals(now, dto.getFechaPago());
    }
}
