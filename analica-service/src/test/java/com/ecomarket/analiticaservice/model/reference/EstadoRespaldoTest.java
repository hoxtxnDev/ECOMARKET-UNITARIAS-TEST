package com.ecomarket.analiticaservice.model.reference;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class EstadoRespaldoTest {

    @Test
    void allArgsConstructorAndGettersWork() {
        EstadoRespaldo er = new EstadoRespaldo(1L, "EXITOSO");
        assertEquals(1L, er.getId());
        assertEquals("EXITOSO", er.getNombre());
    }

    @Test
    void noArgsConstructorAndSettersWork() {
        EstadoRespaldo er = new EstadoRespaldo();
        er.setId(2L);
        er.setNombre("FALLIDO");
        assertEquals(2L, er.getId());
        assertEquals("FALLIDO", er.getNombre());
    }
}
