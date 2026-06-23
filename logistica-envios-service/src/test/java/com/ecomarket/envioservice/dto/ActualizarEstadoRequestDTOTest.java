package com.ecomarket.envioservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ActualizarEstadoRequestDTOTest {

    @Test
    void settersWork() {
        ActualizarEstadoRequestDTO dto = new ActualizarEstadoRequestDTO();
        dto.setNuevoEstadoId(2L);
        dto.setObservacion("Paquete entregado");

        assertEquals(2L, dto.getNuevoEstadoId());
        assertEquals("Paquete entregado", dto.getObservacion());
    }
}
