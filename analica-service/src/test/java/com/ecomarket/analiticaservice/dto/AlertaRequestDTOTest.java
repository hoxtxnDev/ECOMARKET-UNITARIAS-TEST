package com.ecomarket.analiticaservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class AlertaRequestDTOTest {

    @Test
    void constructorAndGettersWork() {
        AlertaRequestDTO dto = new AlertaRequestDTO(1L, "Error crítico", "módulo-test");

        assertEquals(1L, dto.getNivelAlertaId());
        assertEquals("Error crítico", dto.getMensaje());
        assertEquals("módulo-test", dto.getModuloOrigen());
    }

    @Test
    void settersWork() {
        AlertaRequestDTO dto = new AlertaRequestDTO();
        dto.setNivelAlertaId(2L);
        dto.setMensaje("Advertencia");
        dto.setModuloOrigen("otro-modulo");

        assertEquals(2L, dto.getNivelAlertaId());
        assertEquals("Advertencia", dto.getMensaje());
        assertEquals("otro-modulo", dto.getModuloOrigen());
    }
}
