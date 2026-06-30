package com.ecomarket.catalogoinventarioservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MensajeDTOTest {

    @Test
    void constructorAndGettersWork() {
        MensajeDTO dto = new MensajeDTO("Operación exitosa");

        assertEquals("Operación exitosa", dto.getMensaje());
    }
}
