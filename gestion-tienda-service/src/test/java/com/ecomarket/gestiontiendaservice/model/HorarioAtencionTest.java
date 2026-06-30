package com.ecomarket.gestiontiendaservice.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class HorarioAtencionTest {

    @Test
    void allArgsConstructorAndGettersWork() {
        HorarioAtencion h = new HorarioAtencion(1L, 10L, 1, "09:00", "18:00", false);
        assertEquals(1L, h.getId());
        assertEquals(10L, h.getSucursalId());
        assertEquals(1, h.getDiaSemana());
        assertEquals("09:00", h.getHoraApertura());
        assertEquals("18:00", h.getHoraCierre());
        assertFalse(h.getEsFeriado());
    }

    @Test
    void noArgsConstructorAndSettersWork() {
        HorarioAtencion h = new HorarioAtencion();
        h.setId(2L);
        h.setDiaSemana(2);
        h.setEsFeriado(true);
        assertEquals(2, h.getDiaSemana());
        assertTrue(h.getEsFeriado());
    }
}
