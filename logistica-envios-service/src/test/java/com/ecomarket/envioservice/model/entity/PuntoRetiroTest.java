package com.ecomarket.envioservice.model.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PuntoRetiroTest {

    @Test
    void verificarDisponibilidadReturnsTrueWhenActivoAndCapacidadDisponible() {
        PuntoRetiro p = new PuntoRetiro(1L, "Retiro Centro", 5, 10, true);
        assertTrue(p.verificarDisponibilidad());
    }

    @Test
    void verificarDisponibilidadReturnsFalseWhenInactivo() {
        PuntoRetiro p = new PuntoRetiro(1L, "Retiro Centro", 5, 10, false);
        assertFalse(p.verificarDisponibilidad());
    }

    @Test
    void verificarDisponibilidadReturnsFalseWhenCapacidadLlena() {
        PuntoRetiro p = new PuntoRetiro(1L, "Retiro Centro", 10, 10, true);
        assertFalse(p.verificarDisponibilidad());
    }

    @Test
    void verificarDisponibilidadReturnsFalseWhenCapacidadExcedida() {
        PuntoRetiro p = new PuntoRetiro(1L, "Retiro Centro", 15, 10, true);
        assertFalse(p.verificarDisponibilidad());
    }

    @Test
    void allArgsConstructorAndGettersWork() {
        PuntoRetiro p = new PuntoRetiro(1L, "Retiro Norte", 3, 20, true);
        assertEquals(1L, p.getId());
        assertEquals("Retiro Norte", p.getNombre());
        assertEquals(3, p.getCapacidadActual());
        assertEquals(20, p.getCapacidadMaxima());
        assertTrue(p.getActivo());
    }
}
