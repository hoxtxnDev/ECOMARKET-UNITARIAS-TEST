package com.ecomarket.procesopagoservice.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class EstadoPagoTest {

    @Test
    void allArgsConstructorAndGettersWork() {
        EstadoPago ep = new EstadoPago(1L, "COMPLETADO");
        assertEquals(1L, ep.getId());
        assertEquals("COMPLETADO", ep.getNombre());
    }

    @Test
    void noArgsConstructorAndSettersWork() {
        EstadoPago ep = new EstadoPago();
        ep.setId(2L);
        ep.setNombre("RECHAZADO");
        assertEquals(2L, ep.getId());
        assertEquals("RECHAZADO", ep.getNombre());
    }
}
