package com.ecomarket.envioservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

class PlanificarRutaRequestDTOTest {

    @Test
    void settersWork() {
        PlanificarRutaRequestDTO dto = new PlanificarRutaRequestDTO();
        dto.setTransportistaId(1L);
        dto.setEnviosIds(List.of(10L, 20L, 30L));

        assertEquals(1L, dto.getTransportistaId());
        assertEquals(3, dto.getEnviosIds().size());
        assertTrue(dto.getEnviosIds().contains(10L));
    }
}
