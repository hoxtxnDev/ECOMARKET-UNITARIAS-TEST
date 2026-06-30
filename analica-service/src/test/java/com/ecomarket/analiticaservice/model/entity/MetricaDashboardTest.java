package com.ecomarket.analiticaservice.model.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class MetricaDashboardTest {

    static class MetricaDashboardSub extends MetricaDashboard {
        public MetricaDashboardSub(Long id, String claveMetrica, Double valorNumerico, String valorTexto, LocalDateTime ultimaActualizacion) {
            super(id, claveMetrica, valorNumerico, valorTexto, ultimaActualizacion);
        }
        @Override
        public boolean canEqual(Object other) { return other instanceof MetricaDashboardSub; }
    }

    @Test
    void allArgsConstructorAndGetters() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        MetricaDashboard m = new MetricaDashboard(1L, "ventas_totales", 1000.0, "mil", now);
        assertEquals(1L, m.getId());
        assertEquals("ventas_totales", m.getClaveMetrica());
        assertEquals(1000.0, m.getValorNumerico());
        assertEquals("mil", m.getValorTexto());
        assertEquals(now, m.getUltimaActualizacion());
    }

    @Test
    void noArgsConstructorAndSetters() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        MetricaDashboard m = new MetricaDashboard();
        m.setId(2L);
        m.setClaveMetrica("usuarios_nuevos");
        m.setValorNumerico(50.0);
        m.setValorTexto("cincuenta");
        m.setUltimaActualizacion(now);

        assertEquals(2L, m.getId());
        assertEquals("usuarios_nuevos", m.getClaveMetrica());
        assertEquals(50.0, m.getValorNumerico());
        assertEquals("cincuenta", m.getValorTexto());
        assertEquals(now, m.getUltimaActualizacion());
    }

    @Test
    void testToString() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        MetricaDashboard m = new MetricaDashboard(1L, "ventas", 1000.0, "mil", now);
        assertNotNull(m.toString());
    }

    @Test
    void testIdentity() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        MetricaDashboard obj = new MetricaDashboard(1L, "ventas", 1000.0, "mil", now);
        assertTrue(obj.equals(obj));
    }

    @Test
    void testNullAndType() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        MetricaDashboard obj = new MetricaDashboard(1L, "ventas", 1000.0, "mil", now);
        assertFalse(obj.equals(null));
        assertFalse(obj.equals("string"));
    }

    @Test
    void testAllNulls() {
        MetricaDashboard a = new MetricaDashboard(null, null, null, null, null);
        MetricaDashboard b = new MetricaDashboard(null, null, null, null, null);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testFullEquality() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        MetricaDashboard a = new MetricaDashboard(1L, "ventas", 1000.0, "mil", now);
        MetricaDashboard b = new MetricaDashboard(1L, "ventas", 1000.0, "mil", now);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testSequentialMismatches() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);

        assertNotEquals(new MetricaDashboard(null, "clave", 1.0, "txt", now), new MetricaDashboard(1L, "clave", 1.0, "txt", now));
        assertNotEquals(new MetricaDashboard(1L, "clave", 1.0, "txt", now), new MetricaDashboard(null, "clave", 1.0, "txt", now));
        assertNotEquals(new MetricaDashboard(1L, "clave", 1.0, "txt", now), new MetricaDashboard(2L, "clave", 1.0, "txt", now));

        assertNotEquals(new MetricaDashboard(1L, null, 1.0, "txt", now), new MetricaDashboard(1L, "clave", 1.0, "txt", now));
        assertNotEquals(new MetricaDashboard(1L, "clave", 1.0, "txt", now), new MetricaDashboard(1L, null, 1.0, "txt", now));
        assertNotEquals(new MetricaDashboard(1L, "clave", 1.0, "txt", now), new MetricaDashboard(1L, "clave2", 1.0, "txt", now));

        assertNotEquals(new MetricaDashboard(1L, "clave", null, "txt", now), new MetricaDashboard(1L, "clave", 1.0, "txt", now));
        assertNotEquals(new MetricaDashboard(1L, "clave", 1.0, "txt", now), new MetricaDashboard(1L, "clave", null, "txt", now));
        assertNotEquals(new MetricaDashboard(1L, "clave", 1.0, "txt", now), new MetricaDashboard(1L, "clave", 2.0, "txt", now));

        assertNotEquals(new MetricaDashboard(1L, "clave", 1.0, null, now), new MetricaDashboard(1L, "clave", 1.0, "txt", now));
        assertNotEquals(new MetricaDashboard(1L, "clave", 1.0, "txt", now), new MetricaDashboard(1L, "clave", 1.0, null, now));
        assertNotEquals(new MetricaDashboard(1L, "clave", 1.0, "txt", now), new MetricaDashboard(1L, "clave", 1.0, "txt2", now));

        assertNotEquals(new MetricaDashboard(1L, "clave", 1.0, "txt", null), new MetricaDashboard(1L, "clave", 1.0, "txt", now));
        assertNotEquals(new MetricaDashboard(1L, "clave", 1.0, "txt", now), new MetricaDashboard(1L, "clave", 1.0, "txt", null));
        assertNotEquals(new MetricaDashboard(1L, "clave", 1.0, "txt", now), new MetricaDashboard(1L, "clave", 1.0, "txt", now.plusDays(1)));
    }

    @Test
    void testHashCodeCoverage() {
        MetricaDashboard allNull = new MetricaDashboard(null, null, null, null, null);
        allNull.hashCode();
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        MetricaDashboard allNonNull = new MetricaDashboard(1L, "ventas", 1000.0, "mil", now);
        allNonNull.hashCode();
    }

    @Test
    void testSubclassEquality() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        MetricaDashboard base = new MetricaDashboard(1L, "clave", 1.0, "txt", now);
        MetricaDashboardSub sub = new MetricaDashboardSub(1L, "clave", 1.0, "txt", now);
        assertNotEquals(base, sub);
    }
}
