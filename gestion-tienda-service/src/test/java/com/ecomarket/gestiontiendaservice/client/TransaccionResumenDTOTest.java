package com.ecomarket.gestiontiendaservice.client;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class TransaccionResumenDTOTest {

    @Test
    void constructorAndGettersWork() {
        LocalDateTime now = LocalDateTime.now();
        TransaccionResumenDTO dto = new TransaccionResumenDTO(1L, 10L, 5L, 500.0, "APROBADO", now);

        assertEquals(1L, dto.getId());
        assertEquals(10L, dto.getPedidoId());
        assertEquals(5L, dto.getClienteId());
        assertEquals(500.0, dto.getMontoTotal());
        assertEquals("APROBADO", dto.getEstado());
        assertEquals(now, dto.getCodigoAutorizacion());
    }
}
