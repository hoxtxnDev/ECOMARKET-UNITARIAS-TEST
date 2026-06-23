package com.ecomarket.iniciosesion.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MensajeResponseTest {

    @Test
    void constructorAndGettersWork() {
        MensajeResponse dto = new MensajeResponse("Operación exitosa");

        assertEquals("Operación exitosa", dto.getMensaje());
    }

    @Test
    void staticFactoryWorks() {
        MensajeResponse dto = MensajeResponse.de("Mensaje de prueba");

        assertEquals("Mensaje de prueba", dto.getMensaje());
    }
}
