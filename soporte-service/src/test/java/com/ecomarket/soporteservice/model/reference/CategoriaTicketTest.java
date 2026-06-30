package com.ecomarket.soporteservice.model.reference;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CategoriaTicketTest {

    @Test
    void allArgsConstructorAndGettersWork() {
        CategoriaTicket ct = new CategoriaTicket(1L, "PROBLEMA_TECNICO");
        assertEquals(1L, ct.getId());
        assertEquals("PROBLEMA_TECNICO", ct.getNombre());
    }

    @Test
    void noArgsConstructorAndSettersWork() {
        CategoriaTicket ct = new CategoriaTicket();
        ct.setId(2L);
        ct.setNombre("RECLAMO");
        assertEquals(2L, ct.getId());
        assertEquals("RECLAMO", ct.getNombre());
    }
}
