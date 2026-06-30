package com.ecomarket.soporteservice.model.reference;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class EstadoTicketTest {

    @Test
    void allArgsConstructorAndGettersWork() {
        EstadoTicket et = new EstadoTicket(1L, "ABIERTO");
        assertEquals(1L, et.getId());
        assertEquals("ABIERTO", et.getNombre());
    }

    @Test
    void noArgsConstructorAndSettersWork() {
        EstadoTicket et = new EstadoTicket();
        et.setId(2L);
        et.setNombre("CERRADO");
        assertEquals(2L, et.getId());
        assertEquals("CERRADO", et.getNombre());
    }
}
