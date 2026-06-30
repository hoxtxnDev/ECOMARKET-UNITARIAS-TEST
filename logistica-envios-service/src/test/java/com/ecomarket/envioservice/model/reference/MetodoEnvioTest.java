package com.ecomarket.envioservice.model.reference;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MetodoEnvioTest {

    @Test
    void allArgsConstructorAndGettersWork() {
        MetodoEnvio me = new MetodoEnvio(1L, "Domicilio", 5000.0);
        assertEquals(1L, me.getId());
        assertEquals("Domicilio", me.getNombre());
        assertEquals(5000.0, me.getCosto());
    }

    @Test
    void noArgsConstructorAndSettersWork() {
        MetodoEnvio me = new MetodoEnvio();
        me.setId(2L);
        me.setNombre("PuntoRetiro");
        me.setCosto(0.0);
        assertEquals("PuntoRetiro", me.getNombre());
        assertEquals(0.0, me.getCosto());
    }

    @Test
    void costoDefaultIsZero() {
        MetodoEnvio me = new MetodoEnvio();
        assertEquals(0.0, me.getCosto());
    }
}
