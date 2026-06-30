package com.ecomarket.analiticaservice.model.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.ecomarket.analiticaservice.model.reference.EstadoReporte;
import com.ecomarket.analiticaservice.model.reference.TipoReporte;

class ReporteTest {

    static class ReporteSub extends Reporte {
        public ReporteSub(Long id, Long solicitanteId, TipoReporte tipo, EstadoReporte estado, LocalDateTime fechaGeneracion, String urlArchivoResultado, Integer totalRegistrosProcesados) {
            super(id, solicitanteId, tipo, estado, fechaGeneracion, urlArchivoResultado, totalRegistrosProcesados);
        }
        @Override
        public boolean canEqual(Object other) { return other instanceof ReporteSub; }
    }

    @Test
    void allArgsConstructorAndGetters() {
        TipoReporte tipo = new TipoReporte(1L, "VENTAS");
        EstadoReporte estado = new EstadoReporte(1L, "COMPLETADO");
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
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
    void noArgsConstructorAndSetters() {
        TipoReporte tipo = new TipoReporte(2L, "INVENTARIO");
        EstadoReporte estado = new EstadoReporte(2L, "PENDIENTE");
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        Reporte r = new Reporte();
        r.setId(2L);
        r.setSolicitanteId(20L);
        r.setTipo(tipo);
        r.setEstado(estado);
        r.setFechaGeneracion(now);
        r.setUrlArchivoResultado("http://url/other.pdf");
        r.setTotalRegistrosProcesados(50);

        assertEquals(2L, r.getId());
        assertEquals(20L, r.getSolicitanteId());
        assertEquals(tipo, r.getTipo());
        assertEquals(estado, r.getEstado());
        assertEquals(now, r.getFechaGeneracion());
        assertEquals("http://url/other.pdf", r.getUrlArchivoResultado());
        assertEquals(50, r.getTotalRegistrosProcesados());
    }

    @Test
    void testToString() {
        TipoReporte tipo = new TipoReporte(1L, "VENTAS");
        EstadoReporte estado = new EstadoReporte(1L, "COMPLETADO");
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        Reporte r = new Reporte(1L, 10L, tipo, estado, now, "url", 100);
        assertNotNull(r.toString());
    }

    @Test
    void testIdentity() {
        TipoReporte tipo = new TipoReporte(1L, "VENTAS");
        EstadoReporte estado = new EstadoReporte(1L, "COMPLETADO");
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        Reporte obj = new Reporte(1L, 10L, tipo, estado, now, "url", 100);
        assertTrue(obj.equals(obj));
    }

    @Test
    void testNullAndType() {
        TipoReporte tipo = new TipoReporte(1L, "VENTAS");
        EstadoReporte estado = new EstadoReporte(1L, "COMPLETADO");
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        Reporte obj = new Reporte(1L, 10L, tipo, estado, now, "url", 100);
        assertFalse(obj.equals(null));
        assertFalse(obj.equals("string"));
    }

    @Test
    void testAllNulls() {
        Reporte a = new Reporte(null, null, null, null, null, null, null);
        Reporte b = new Reporte(null, null, null, null, null, null, null);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testFullEquality() {
        TipoReporte tipo = new TipoReporte(1L, "VENTAS");
        EstadoReporte estado = new EstadoReporte(1L, "COMPLETADO");
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        Reporte a = new Reporte(1L, 10L, tipo, estado, now, "url", 100);
        Reporte b = new Reporte(1L, 10L, tipo, estado, now, "url", 100);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testSequentialMismatches() {
        TipoReporte tipo = new TipoReporte(1L, "VENTAS");
        EstadoReporte estado = new EstadoReporte(1L, "COMPLETADO");
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        String url = "url";

        assertNotEquals(new Reporte(null, 10L, tipo, estado, now, url, 100), new Reporte(1L, 10L, tipo, estado, now, url, 100));
        assertNotEquals(new Reporte(1L, 10L, tipo, estado, now, url, 100), new Reporte(null, 10L, tipo, estado, now, url, 100));
        assertNotEquals(new Reporte(1L, 10L, tipo, estado, now, url, 100), new Reporte(2L, 10L, tipo, estado, now, url, 100));

        assertNotEquals(new Reporte(1L, null, tipo, estado, now, url, 100), new Reporte(1L, 10L, tipo, estado, now, url, 100));
        assertNotEquals(new Reporte(1L, 10L, tipo, estado, now, url, 100), new Reporte(1L, null, tipo, estado, now, url, 100));
        assertNotEquals(new Reporte(1L, 10L, tipo, estado, now, url, 100), new Reporte(1L, 20L, tipo, estado, now, url, 100));

        assertNotEquals(new Reporte(1L, 10L, null, estado, now, url, 100), new Reporte(1L, 10L, tipo, estado, now, url, 100));
        assertNotEquals(new Reporte(1L, 10L, tipo, estado, now, url, 100), new Reporte(1L, 10L, null, estado, now, url, 100));
        assertNotEquals(new Reporte(1L, 10L, tipo, estado, now, url, 100), new Reporte(1L, 10L, new TipoReporte(2L, "INV"), estado, now, url, 100));

        assertNotEquals(new Reporte(1L, 10L, tipo, null, now, url, 100), new Reporte(1L, 10L, tipo, estado, now, url, 100));
        assertNotEquals(new Reporte(1L, 10L, tipo, estado, now, url, 100), new Reporte(1L, 10L, tipo, null, now, url, 100));
        assertNotEquals(new Reporte(1L, 10L, tipo, estado, now, url, 100), new Reporte(1L, 10L, tipo, new EstadoReporte(2L, "PEN"), now, url, 100));

        assertNotEquals(new Reporte(1L, 10L, tipo, estado, null, url, 100), new Reporte(1L, 10L, tipo, estado, now, url, 100));
        assertNotEquals(new Reporte(1L, 10L, tipo, estado, now, url, 100), new Reporte(1L, 10L, tipo, estado, null, url, 100));
        assertNotEquals(new Reporte(1L, 10L, tipo, estado, now, url, 100), new Reporte(1L, 10L, tipo, estado, now.plusDays(1), url, 100));

        assertNotEquals(new Reporte(1L, 10L, tipo, estado, now, null, 100), new Reporte(1L, 10L, tipo, estado, now, url, 100));
        assertNotEquals(new Reporte(1L, 10L, tipo, estado, now, url, 100), new Reporte(1L, 10L, tipo, estado, now, null, 100));
        assertNotEquals(new Reporte(1L, 10L, tipo, estado, now, url, 100), new Reporte(1L, 10L, tipo, estado, now, "url2", 100));

        assertNotEquals(new Reporte(1L, 10L, tipo, estado, now, url, null), new Reporte(1L, 10L, tipo, estado, now, url, 100));
        assertNotEquals(new Reporte(1L, 10L, tipo, estado, now, url, 100), new Reporte(1L, 10L, tipo, estado, now, url, null));
        assertNotEquals(new Reporte(1L, 10L, tipo, estado, now, url, 100), new Reporte(1L, 10L, tipo, estado, now, url, 200));
    }

    @Test
    void testHashCodeCoverage() {
        Reporte allNull = new Reporte(null, null, null, null, null, null, null);
        allNull.hashCode();
        TipoReporte tipo = new TipoReporte(1L, "VENTAS");
        EstadoReporte estado = new EstadoReporte(1L, "COMPLETADO");
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        Reporte allNonNull = new Reporte(1L, 10L, tipo, estado, now, "url", 100);
        allNonNull.hashCode();
    }

    @Test
    void testSubclassEquality() {
        TipoReporte tipo = new TipoReporte(1L, "VENTAS");
        EstadoReporte estado = new EstadoReporte(1L, "COMPLETADO");
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        Reporte base = new Reporte(1L, 10L, tipo, estado, now, "url", 100);
        ReporteSub sub = new ReporteSub(1L, 10L, tipo, estado, now, "url", 100);
        assertNotEquals(base, sub);
    }
}
