package com.ecomarket.analiticaservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

class ReporteResultadoDTOTest {

    @Test
    void constructorAndGettersWork() {
        ReporteResultadoDTO dto = new ReporteResultadoDTO(
                "USUARIOS", 5L,
                List.of(Map.of("id", 1)),
                List.of("servicio1")
        );

        assertEquals("USUARIOS", dto.getTipoReporte());
        assertEquals(5L, dto.getTotalRegistros());
        assertEquals(1, dto.getDatos().size());
        assertEquals(1, dto.getServiciosNoDisponibles().size());
    }
}
