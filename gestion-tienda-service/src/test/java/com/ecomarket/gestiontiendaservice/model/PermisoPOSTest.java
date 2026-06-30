package com.ecomarket.gestiontiendaservice.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PermisoPOSTest {

    @Test
    void allArgsConstructorAndGettersWork() {
        PermisoPOS p = new PermisoPOS(1L, 10L, true, false, true);
        assertEquals(1L, p.getId());
        assertEquals(10L, p.getRolEmpleado());
        assertTrue(p.getPermiteAnulaciones());
        assertFalse(p.getPermiteAperturaCaja());
        assertTrue(p.getPermiteAplicarDescuentoManual());
    }

    @Test
    void noArgsConstructorAndSettersWork() {
        PermisoPOS p = new PermisoPOS();
        p.setId(2L);
        p.setRolEmpleado(5L);
        p.setPermiteAnulaciones(false);
        assertFalse(p.getPermiteAnulaciones());
        assertEquals(5L, p.getRolEmpleado());
    }
}
