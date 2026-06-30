package com.ecomarket.envioservice.model.reference;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class EstadoEnvioTest {

    @Test
    void allArgsConstructorAndGettersWork() {
        EstadoEnvio ee = new EstadoEnvio(1L, "PENDIENTE");
        assertEquals(1L, ee.getId());
        assertEquals("PENDIENTE", ee.getNombre());
    }

    @Test
    void noArgsConstructorAndSettersWork() {
        EstadoEnvio ee = new EstadoEnvio();
        ee.setId(2L);
        ee.setNombre("EN_CAMINO");
        assertEquals(2L, ee.getId());
        assertEquals("EN_CAMINO", ee.getNombre());
    }
}
