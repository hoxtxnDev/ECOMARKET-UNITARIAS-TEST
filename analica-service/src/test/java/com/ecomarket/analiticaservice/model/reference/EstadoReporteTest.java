package com.ecomarket.analiticaservice.model.reference;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class EstadoReporteTest {

    static class EstadoReporteSub extends EstadoReporte {
        public EstadoReporteSub(Long id, String nombre) { super(id, nombre); }
        @Override
        public boolean canEqual(Object other) { return other instanceof EstadoReporteSub; }
    }

    @Test
    void allArgsConstructorAndGetters() {
        EstadoReporte er = new EstadoReporte(1L, "COMPLETADO");
        assertEquals(1L, er.getId());
        assertEquals("COMPLETADO", er.getNombre());
    }

    @Test
    void noArgsConstructorAndSetters() {
        EstadoReporte er = new EstadoReporte();
        er.setId(2L);
        er.setNombre("PENDIENTE");

        assertEquals(2L, er.getId());
        assertEquals("PENDIENTE", er.getNombre());
    }

    @Test
    void testToString() {
        EstadoReporte er = new EstadoReporte(1L, "COMPLETADO");
        assertNotNull(er.toString());
    }

    @Test
    void testIdentity() {
        EstadoReporte obj = new EstadoReporte(1L, "TEST");
        assertTrue(obj.equals(obj));
    }

    @Test
    void testNullAndType() {
        EstadoReporte obj = new EstadoReporte(1L, "TEST");
        assertFalse(obj.equals(null));
        assertFalse(obj.equals("string"));
    }

    @Test
    void testAllNulls() {
        EstadoReporte a = new EstadoReporte(null, null);
        EstadoReporte b = new EstadoReporte(null, null);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testFullEquality() {
        EstadoReporte a = new EstadoReporte(1L, "TEST");
        EstadoReporte b = new EstadoReporte(1L, "TEST");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testSequentialMismatches() {
        assertNotEquals(new EstadoReporte(null, "TEST"), new EstadoReporte(1L, "TEST"));
        assertNotEquals(new EstadoReporte(1L, "TEST"), new EstadoReporte(null, "TEST"));
        assertNotEquals(new EstadoReporte(1L, "TEST"), new EstadoReporte(2L, "TEST"));

        assertNotEquals(new EstadoReporte(1L, null), new EstadoReporte(1L, "TEST"));
        assertNotEquals(new EstadoReporte(1L, "TEST"), new EstadoReporte(1L, null));
        assertNotEquals(new EstadoReporte(1L, "TEST"), new EstadoReporte(1L, "OTHER"));
    }

    @Test
    void testHashCodeCoverage() {
        EstadoReporte allNull = new EstadoReporte(null, null);
        allNull.hashCode();
        EstadoReporte allNonNull = new EstadoReporte(1L, "TEST");
        allNonNull.hashCode();
    }

    @Test
    void testSubclassEquality() {
        EstadoReporte base = new EstadoReporte(1L, "TEST");
        EstadoReporteSub sub = new EstadoReporteSub(1L, "TEST");
        assertNotEquals(base, sub);
    }
}
