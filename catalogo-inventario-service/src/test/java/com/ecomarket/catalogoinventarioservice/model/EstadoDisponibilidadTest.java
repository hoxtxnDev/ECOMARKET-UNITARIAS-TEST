package com.ecomarket.catalogoinventarioservice.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class EstadoDisponibilidadTest {

    @Test
    void allArgsConstructorAndGettersWork() {
        EstadoDisponibilidad ed = new EstadoDisponibilidad(1L, "DISPONIBLE");
        assertEquals(1L, ed.getId());
        assertEquals("DISPONIBLE", ed.getNombre());
    }

    @Test
    void noArgsConstructorAndSettersWork() {
        EstadoDisponibilidad ed = new EstadoDisponibilidad();
        ed.setId(2L);
        ed.setNombre("AGOTADO");
        assertEquals(2L, ed.getId());
        assertEquals("AGOTADO", ed.getNombre());
    }
}
