package com.ecomarket.analiticaservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ReporteRequestDTOTest {

    @Test
    void constructorAndGettersWork() {
        ReporteRequestDTO dto = new ReporteRequestDTO(1L, 3L);

        assertEquals(1L, dto.getSolicitanteId());
        assertEquals(3L, dto.getTipoReporteId());
    }
}
