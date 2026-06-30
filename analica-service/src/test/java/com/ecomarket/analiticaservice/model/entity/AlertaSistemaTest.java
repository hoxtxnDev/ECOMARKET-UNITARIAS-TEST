package com.ecomarket.analiticaservice.model.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.ecomarket.analiticaservice.model.reference.NivelAlerta;

class AlertaSistemaTest {

    @Test
    void allArgsConstructorAndGettersWork() {
        NivelAlerta nivel = new NivelAlerta(1L, "CRITICO");
        LocalDateTime now = LocalDateTime.now();
        AlertaSistema a = new AlertaSistema(1L, nivel, "Error crítico", "Módulo ventas", now, false);
        assertEquals(1L, a.getId());
        assertEquals(nivel, a.getNivel());
        assertEquals("Error crítico", a.getMensaje());
        assertEquals("Módulo ventas", a.getModuloOrigen());
        assertEquals(now, a.getFechaAlerta());
        assertFalse(a.getResuelta());
    }

    @Test
    void noArgsConstructorAndSettersWork() {
        AlertaSistema a = new AlertaSistema();
        a.setId(2L);
        a.setMensaje("Advertencia");
        a.setResuelta(true);
        assertEquals("Advertencia", a.getMensaje());
        assertTrue(a.getResuelta());
    }
}
