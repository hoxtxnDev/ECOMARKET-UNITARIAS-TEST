package com.ecomarket.analiticaservice.model.reference;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class EstadoReporteTest {

    @Test
    void allArgsConstructorAndGettersWork() {
        EstadoReporte er = new EstadoReporte(1L, "COMPLETADO");
        assertEquals(1L, er.getId());
        assertEquals("COMPLETADO", er.getNombre());
    }

    @Test
    void noArgsConstructorAndSettersWork() {
        EstadoReporte er = new EstadoReporte();
        er.setId(2L);
        er.setNombre("PENDIENTE");
        assertEquals(2L, er.getId());
        assertEquals("PENDIENTE", er.getNombre());
    }
}
