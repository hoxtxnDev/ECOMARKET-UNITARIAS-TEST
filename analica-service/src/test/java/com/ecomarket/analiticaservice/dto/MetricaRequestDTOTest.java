package com.ecomarket.analiticaservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MetricaRequestDTOTest {

    @Test
    void constructorAndGettersWork() {
        MetricaRequestDTO dto = new MetricaRequestDTO("ventas_diarias", 1500.0, "pico");

        assertEquals("ventas_diarias", dto.getClaveMetrica());
        assertEquals(1500.0, dto.getValorNumerico());
        assertEquals("pico", dto.getValorTexto());
    }
}
