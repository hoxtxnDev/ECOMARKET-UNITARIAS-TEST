package com.ecomarket.envioservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class RegistrarRecepcionRequestDTOTest {

    @Test
    void settersWork() {
        RegistrarRecepcionRequestDTO dto = new RegistrarRecepcionRequestDTO();
        dto.setFirmaRecibe("Juan Pérez");

        assertEquals("Juan Pérez", dto.getFirmaRecibe());
    }
}
