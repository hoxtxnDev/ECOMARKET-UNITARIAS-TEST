package com.ecomarket.envioservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SeleccionarPuntoRetiroRequestDTOTest {

    @Test
    void settersWork() {
        SeleccionarPuntoRetiroRequestDTO dto = new SeleccionarPuntoRetiroRequestDTO();
        dto.setPuntoRetiroId(5L);
        dto.setFirmaRecibe("María López");

        assertEquals(5L, dto.getPuntoRetiroId());
        assertEquals("María López", dto.getFirmaRecibe());
    }
}
