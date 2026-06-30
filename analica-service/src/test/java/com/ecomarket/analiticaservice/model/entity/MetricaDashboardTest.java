package com.ecomarket.analiticaservice.model.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class MetricaDashboardTest {

    @Test
    void allArgsConstructorAndGettersWork() {
        LocalDateTime now = LocalDateTime.now();
        MetricaDashboard m = new MetricaDashboard(1L, "ventas_totales", 1000.0, "mil", now);
        assertEquals(1L, m.getId());
        assertEquals("ventas_totales", m.getClaveMetrica());
        assertEquals(1000.0, m.getValorNumerico());
        assertEquals("mil", m.getValorTexto());
        assertEquals(now, m.getUltimaActualizacion());
    }

    @Test
    void noArgsConstructorAndSettersWork() {
        MetricaDashboard m = new MetricaDashboard();
        m.setId(2L);
        m.setClaveMetrica("usuarios_nuevos");
        m.setValorNumerico(50.0);
        assertEquals("usuarios_nuevos", m.getClaveMetrica());
        assertEquals(50.0, m.getValorNumerico());
    }
}
