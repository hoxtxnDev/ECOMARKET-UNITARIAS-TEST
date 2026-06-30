package com.ecomarket.gestiontiendaservice.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class EstadoTareaPersonalTest {

    @Test
    void allArgsConstructorAndGettersWork() {
        EstadoTareaPersonal et = new EstadoTareaPersonal(1L, "PENDIENTE");
        assertEquals(1L, et.getId());
        assertEquals("PENDIENTE", et.getNombre());
    }

    @Test
    void noArgsConstructorAndSettersWork() {
        EstadoTareaPersonal et = new EstadoTareaPersonal();
        et.setId(2L);
        et.setNombre("COMPLETADA");
        assertEquals(2L, et.getId());
        assertEquals("COMPLETADA", et.getNombre());
    }
}
