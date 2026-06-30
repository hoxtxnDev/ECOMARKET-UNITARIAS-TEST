package com.ecomarket.gestiontiendaservice.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class SucursalTest {

    @Test
    void allArgsConstructorAndGettersWork() {
        LocalDateTime now = LocalDateTime.now();
        Sucursal s = new Sucursal(1L, "Sucursal Centro", "Calle 123", "123456789", 5L, true, now);
        assertEquals(1L, s.getId());
        assertEquals("Sucursal Centro", s.getNombre());
        assertEquals("Calle 123", s.getDireccion());
        assertEquals("123456789", s.getTelefono());
        assertEquals(5L, s.getGerenteCargoId());
        assertTrue(s.getActiva());
        assertEquals(now, s.getFechaInauguracion());
    }

    @Test
    void noArgsConstructorAndSettersWork() {
        Sucursal s = new Sucursal();
        s.setId(2L);
        s.setNombre("Sucursal Norte");
        s.setActiva(false);
        assertEquals("Sucursal Norte", s.getNombre());
        assertFalse(s.getActiva());
    }
}
