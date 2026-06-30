package com.ecomarket.procesopagoservice.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MetodoPagoTransaccionTest {

    @Test
    void allArgsConstructorAndGettersWork() {
        MetodoPagoTransaccion m = new MetodoPagoTransaccion(1L, "TARJETA_CREDITO");
        assertEquals(1L, m.getId());
        assertEquals("TARJETA_CREDITO", m.getNombre());
    }

    @Test
    void noArgsConstructorAndSettersWork() {
        MetodoPagoTransaccion m = new MetodoPagoTransaccion();
        m.setId(2L);
        m.setNombre("DEBITO");
        assertEquals(2L, m.getId());
        assertEquals("DEBITO", m.getNombre());
    }
}
