package com.ecomarket.analiticaservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class EnvioDTOTest {

    @Test
    void constructorAndGettersWork() {
        LocalDateTime now = LocalDateTime.now();
        EnvioDTO dto = new EnvioDTO(1L, 10L, 100L, "ENVIADO", "DOMICILIO", now);

        assertEquals(1L, dto.getId());
        assertEquals(10L, dto.getPedidoId());
        assertEquals(100L, dto.getTransportistaId());
        assertEquals("ENVIADO", dto.getEstado());
        assertEquals("DOMICILIO", dto.getMetodoEnvio());
        assertEquals(now, dto.getFechaCreacion());
    }
}
