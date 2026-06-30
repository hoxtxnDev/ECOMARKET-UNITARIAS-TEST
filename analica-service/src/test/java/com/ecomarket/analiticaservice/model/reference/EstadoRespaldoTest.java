package com.ecomarket.analiticaservice.model.reference;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class EstadoRespaldoTest {

    static class EstadoRespaldoSub extends EstadoRespaldo {
        public EstadoRespaldoSub(Long id, String nombre) { super(id, nombre); }
        @Override
        public boolean canEqual(Object other) { return other instanceof EstadoRespaldoSub; }
    }

    @Test
    void allArgsConstructorAndGetters() {
        EstadoRespaldo er = new EstadoRespaldo(1L, "EXITOSO");
        assertEquals(1L, er.getId());
        assertEquals("EXITOSO", er.getNombre());
    }

    @Test
    void noArgsConstructorAndSetters() {
        EstadoRespaldo er = new EstadoRespaldo();
        er.setId(2L);
        er.setNombre("FALLIDO");

        assertEquals(2L, er.getId());
        assertEquals("FALLIDO", er.getNombre());
    }

    @Test
    void testToString() {
        EstadoRespaldo er = new EstadoRespaldo(1L, "EXITOSO");
        assertNotNull(er.toString());
    }

    @Test
    void testIdentity() {
        EstadoRespaldo obj = new EstadoRespaldo(1L, "TEST");
        assertTrue(obj.equals(obj));
    }

    @Test
    void testNullAndType() {
        EstadoRespaldo obj = new EstadoRespaldo(1L, "TEST");
        assertFalse(obj.equals(null));
        assertFalse(obj.equals("string"));
    }

    @Test
    void testAllNulls() {
        EstadoRespaldo a = new EstadoRespaldo(null, null);
        EstadoRespaldo b = new EstadoRespaldo(null, null);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testFullEquality() {
        EstadoRespaldo a = new EstadoRespaldo(1L, "TEST");
        EstadoRespaldo b = new EstadoRespaldo(1L, "TEST");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testSequentialMismatches() {
        assertNotEquals(new EstadoRespaldo(null, "TEST"), new EstadoRespaldo(1L, "TEST"));
        assertNotEquals(new EstadoRespaldo(1L, "TEST"), new EstadoRespaldo(null, "TEST"));
        assertNotEquals(new EstadoRespaldo(1L, "TEST"), new EstadoRespaldo(2L, "TEST"));

        assertNotEquals(new EstadoRespaldo(1L, null), new EstadoRespaldo(1L, "TEST"));
        assertNotEquals(new EstadoRespaldo(1L, "TEST"), new EstadoRespaldo(1L, null));
        assertNotEquals(new EstadoRespaldo(1L, "TEST"), new EstadoRespaldo(1L, "OTHER"));
    }

    @Test
    void testHashCodeCoverage() {
        EstadoRespaldo allNull = new EstadoRespaldo(null, null);
        allNull.hashCode();
        EstadoRespaldo allNonNull = new EstadoRespaldo(1L, "TEST");
        allNonNull.hashCode();
    }

    @Test
    void testSubclassEquality() {
        EstadoRespaldo base = new EstadoRespaldo(1L, "TEST");
        EstadoRespaldoSub sub = new EstadoRespaldoSub(1L, "TEST");
        assertNotEquals(base, sub);
    }
}
