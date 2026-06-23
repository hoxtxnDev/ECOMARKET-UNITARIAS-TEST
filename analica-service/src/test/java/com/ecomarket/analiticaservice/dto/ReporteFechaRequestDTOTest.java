package com.ecomarket.analiticaservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class ReporteFechaRequestDTOTest {

    @Test
    void constructorAndGettersWork() {
        LocalDateTime inicio = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 6, 30, 23, 59);
        ReporteFechaRequestDTO dto = new ReporteFechaRequestDTO(1L, 2L, inicio, fin);

        assertEquals(1L, dto.getSolicitanteId());
        assertEquals(2L, dto.getTipoReporteId());
        assertEquals(inicio, dto.getFechaInicio());
        assertEquals(fin, dto.getFechaFin());
    }
}
