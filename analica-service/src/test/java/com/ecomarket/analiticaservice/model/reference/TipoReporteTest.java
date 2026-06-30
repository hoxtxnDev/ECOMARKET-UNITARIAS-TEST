package com.ecomarket.analiticaservice.model.reference;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TipoReporteTest {

    static class TipoReporteSub extends TipoReporte {
        public TipoReporteSub(Long id, String nombre) { super(id, nombre); }
        @Override
        public boolean canEqual(Object other) { return other instanceof TipoReporteSub; }
    }

    @Test
    void allArgsConstructorAndGetters() {
        TipoReporte tr = new TipoReporte(1L, "VENTAS");
        assertEquals(1L, tr.getId());
        assertEquals("VENTAS", tr.getNombre());
    }

    @Test
    void noArgsConstructorAndSetters() {
        TipoReporte tr = new TipoReporte();
        tr.setId(2L);
        tr.setNombre("INVENTARIO");

        assertEquals(2L, tr.getId());
        assertEquals("INVENTARIO", tr.getNombre());
    }

    @Test
    void testToString() {
        TipoReporte tr = new TipoReporte(1L, "VENTAS");
        assertNotNull(tr.toString());
    }

    @Test
    void testIdentity() {
        TipoReporte obj = new TipoReporte(1L, "TEST");
        assertTrue(obj.equals(obj));
    }

    @Test
    void testNullAndType() {
        TipoReporte obj = new TipoReporte(1L, "TEST");
        assertFalse(obj.equals(null));
        assertFalse(obj.equals("string"));
    }

    @Test
    void testAllNulls() {
        TipoReporte a = new TipoReporte(null, null);
        TipoReporte b = new TipoReporte(null, null);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testFullEquality() {
        TipoReporte a = new TipoReporte(1L, "TEST");
        TipoReporte b = new TipoReporte(1L, "TEST");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testSequentialMismatches() {
        assertNotEquals(new TipoReporte(null, "TEST"), new TipoReporte(1L, "TEST"));
        assertNotEquals(new TipoReporte(1L, "TEST"), new TipoReporte(null, "TEST"));
        assertNotEquals(new TipoReporte(1L, "TEST"), new TipoReporte(2L, "TEST"));

        assertNotEquals(new TipoReporte(1L, null), new TipoReporte(1L, "TEST"));
        assertNotEquals(new TipoReporte(1L, "TEST"), new TipoReporte(1L, null));
        assertNotEquals(new TipoReporte(1L, "TEST"), new TipoReporte(1L, "OTHER"));
    }

    @Test
    void testHashCodeCoverage() {
        TipoReporte allNull = new TipoReporte(null, null);
        allNull.hashCode();
        TipoReporte allNonNull = new TipoReporte(1L, "TEST");
        allNonNull.hashCode();
    }

    @Test
    void testSubclassEquality() {
        TipoReporte base = new TipoReporte(1L, "TEST");
        TipoReporteSub sub = new TipoReporteSub(1L, "TEST");
        assertNotEquals(base, sub);
    }
}
