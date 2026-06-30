package com.ecomarket.analiticaservice.model.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.ecomarket.analiticaservice.model.reference.EstadoReporte;
import com.ecomarket.analiticaservice.model.reference.TipoReporte;

class ReporteTest {

    @Test
    void allArgsConstructorAndGettersWork() {
        TipoReporte tipo = new TipoReporte(1L, "VENTAS");
        EstadoReporte estado = new EstadoReporte(1L, "COMPLETADO");
        LocalDateTime now = LocalDateTime.now();
        Reporte r = new Reporte(1L, 10L, tipo, estado, now, "http://url/reporte.pdf", 100);
        assertEquals(1L, r.getId());
        assertEquals(10L, r.getSolicitanteId());
        assertEquals(tipo, r.getTipo());
        assertEquals(estado, r.getEstado());
        assertEquals(now, r.getFechaGeneracion());
        assertEquals("http://url/reporte.pdf", r.getUrlArchivoResultado());
        assertEquals(100, r.getTotalRegistrosProcesados());
    }

    @Test
    void noArgsConstructorAndSettersWork() {
        Reporte r = new Reporte();
        r.setId(2L);
        r.setTotalRegistrosProcesados(50);
        assertEquals(50, r.getTotalRegistrosProcesados());
    }
}
